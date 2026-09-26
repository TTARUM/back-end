import "reflect-metadata";
import { INestApplication } from "@nestjs/common";
import { ConfigModule } from "@nestjs/config";
import { Test } from "@nestjs/testing";
import { TypeOrmModule } from "@nestjs/typeorm";
import { DataSource } from "typeorm";
import request from "supertest";
import { JwtService } from "@nestjs/jwt";
import { AuthModule } from "../src/auth/auth.module";
import { validationPipe } from "../src/common/http";
import {
  entities,
  Member,
  MemberProvider,
  OauthMember,
  Coupon,
  MemberCoupon,
} from "../src/database/entities";

describe("Kakao authentication", () => {
  let app: INestApplication;
  let db: DataSource;
  let providerId: number;
  let mock: jest.SpyInstance;
  let subject = 123;
  let email = "kakao@example.com";
  let nickname: string | undefined = "와인친구";
  let appId = 960004;
  let verified = true;
  beforeAll(async () => {
    const module = await Test.createTestingModule({
      imports: [
        ConfigModule.forRoot({
          isGlobal: true,
          ignoreEnvFile: true,
          load: [
            () => ({
              JWT_SECRET_KEY: "test-secret-at-least-32-bytes-long",
              KAKAO_APP_ID: "960004",
            }),
          ],
        }),
        TypeOrmModule.forRoot({ type: "sqljs", entities, synchronize: true }),
        AuthModule,
      ],
    }).compile();
    app = module.createNestApplication();
    app.setGlobalPrefix("api");
    app.useGlobalPipes(validationPipe());
    await app.init();
    db = app.get(DataSource);
    providerId = (
      await db.getRepository(MemberProvider).save({ name: "KAKAO" })
    ).id;
    await db
      .getRepository(Coupon)
      .save({ id: 1, name: "가입", couponStrategy: "PERCENTAGE", value: 10 });
    mock = jest
      .spyOn(global, "fetch")
      .mockImplementation(
        async (input) =>
          new Response(
            JSON.stringify(
              String(input).endsWith("access_token_info")
                ? { id: subject, app_id: appId, expires_in: 3600 }
                : {
                    id: subject,
                    kakao_account: {
                      email,
                      is_email_valid: true,
                      is_email_verified: verified,
                      profile: { nickname },
                    },
                  },
            ),
            { status: 200 },
          ),
      );
  });
  afterAll(async () => {
    mock?.mockRestore();
    await app?.close();
  });
  const login = () =>
    request(app.getHttpServer())
      .post("/api/auth/kakao/login")
      .send({ accessToken: "test-kakao-token" });
  const register = (extra = {}) =>
    request(app.getHttpServer())
      .post("/api/auth/kakao/register")
      .send({
        accessToken: "test-kakao-token",
        name: "홍길동",
        phoneNumber: "01012345678",
        ...extra,
      });

  it("returns a verified nickname and email for a new account without creating a member", async () => {
    const response = await login().expect(200);
    expect(response.body).toEqual({
      needsRegistration: true,
      profile: { nickname: "와인친구", email },
    });
    expect(await db.getRepository(Member).count()).toBe(0);
  });
  it("rejects client-supplied nickname and email", async () => {
    await register({ nickname: "조작", email: "fake@example.com" }).expect(400);
    expect(await db.getRepository(Member).count()).toBe(0);
  });
  it("creates member and oauth account together, grants coupon and issues a service token", async () => {
    const response = await register().expect(200);
    expect(response.body.needsRegistration).toBe(false);
    const claims = await app
      .get(JwtService)
      .verifyAsync(response.body.user.token);
    const account = await db
      .getRepository(OauthMember)
      .findOneByOrFail({ memberId: Number(claims.sub) });
    expect(account).toMatchObject({
      email,
      providerId,
      providerSubject: "123",
    });
    expect(response.body.user.name).toBe("홍길동");
    expect(response.body.user.nickname).toBe("와인친구");
    expect(
      await db
        .getRepository(MemberCoupon)
        .countBy({ memberId: account.memberId }),
    ).toBe(1);
  });
  it("logs existing accounts in and makes sequential registration retries idempotent", async () => {
    expect((await login().expect(200)).body.user.token).toBeTruthy();
    await register().expect(200);
    expect(await db.getRepository(Member).count()).toBe(1);
    expect(await db.getRepository(MemberCoupon).count()).toBe(1);
  });
  it("rejects a token issued to another app", async () => {
    appId = 1;
    await login().expect(401);
    appId = 960004;
  });
  it("rejects unverified email", async () => {
    verified = false;
    await login().expect(400);
    verified = true;
  });
  it("rejects missing Kakao nickname on signup", async () => {
    subject = 124;
    email = "new@example.com";
    nickname = undefined;
    await login().expect(400);
    await register().expect(400);
    nickname = "새회원";
  });
  it("does not take over an account solely because its email matches", async () => {
    email = "kakao@example.com";
    expect((await login().expect(200)).body.needsRegistration).toBe(true);
    await register().expect(409);
  });
  it("rejects deleted members", async () => {
    subject = 123;
    const account = await db
      .getRepository(OauthMember)
      .findOneByOrFail({ providerSubject: "123", providerId });
    await db
      .getRepository(Member)
      .update(account.memberId, { isDeleted: true });
    await login().expect(401);
  });
});
