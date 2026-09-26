import "reflect-metadata";
import { INestApplication } from "@nestjs/common";
import { ConfigModule } from "@nestjs/config";
import { Test } from "@nestjs/testing";
import { TypeOrmModule } from "@nestjs/typeorm";
import { DataSource } from "typeorm";
import request from "supertest";
import { AuthModule } from "../src/auth/auth.module";
import { ApiExceptionFilter, validationPipe } from "../src/common/http";
import {
  Category,
  Coupon,
  entities,
  Item,
  Member,
  MemberCoupon,
  Order,
  OrderItem,
  Review,
} from "../src/database/entities";
import { ItemModule } from "../src/item/item.module";
import { MemberModule } from "../src/member/member.module";
import { OrderModule } from "../src/order/order.module";
import { ReviewModule } from "../src/review/review.module";
import { InquiryModule } from "../src/inquiry/inquiry.module";
import { StorageModule, StorageService } from "../src/storage/storage.module";
import { MailService } from "../src/member/mail.service";
describe("Nest API integration", () => {
  let app: INestApplication;
  let db: DataSource;
  let token: string;
  let memberId: number;
  let itemId: number;
  const storage = { upload: jest.fn(async () => []), cleanup: jest.fn() };
  beforeAll(async () => {
    const module = await Test.createTestingModule({
      imports: [
        ConfigModule.forRoot({
          isGlobal: true,
          ignoreEnvFile: true,
          load: [
            () => ({
              JWT_SECRET_KEY: "test-secret-key-with-at-least-32-bytes",
            }),
          ],
        }),
        TypeOrmModule.forRoot(
          process.env.TEST_MYSQL === "1"
            ? {
                type: "mysql",
                host: "127.0.0.1",
                port: 3306,
                username: "root",
                password: "test",
                database: "ttarum_test",
                entities,
                synchronize: true,
                dropSchema: true,
              }
            : { type: "sqljs", entities, synchronize: true },
        ),
        AuthModule,
        StorageModule,
        ItemModule,
        MemberModule,
        OrderModule,
        ReviewModule,
        InquiryModule,
      ],
    })
      .overrideProvider(StorageService)
      .useValue(storage)
      .overrideProvider(MailService)
      .useValue({})
      .compile();
    app = module.createNestApplication();
    app.setGlobalPrefix("api");
    app.useGlobalPipes(validationPipe());
    app.useGlobalFilters(new ApiExceptionFilter());
    await app.init();
    db = app.get(DataSource);
    await db
      .getRepository(Coupon)
      .save({
        id: 1,
        name: "신규 가입 쿠폰",
        couponStrategy: "PERCENTAGE",
        value: 10,
      });
    const category = await db.getRepository(Category).save({ name: "와인" });
    const item = await db
      .getRepository(Item)
      .save({
        name: "테스트 와인",
        description: "설명",
        price: 20000,
        categoryId: category.id,
        itemImageUrl: "https://example.com/wine.jpg",
      });
    itemId = item.id;
    const registered = await request(app.getHttpServer())
      .post("/api/members/register")
      .send({
        name: "홍길동",
        nickname: "와인조아",
        phoneNumber: "010-1234-5678",
        loginId: "tester",
        password: "Test1234!",
        email: "test@example.com",
      })
      .expect(200);
    memberId = registered.body.memberId;
    const login = await request(app.getHttpServer())
      .post("/api/auth/login")
      .send({ loginId: "tester", password: "Test1234!" })
      .expect(200);
    token = login.body.token;
  });
  afterAll(async () => {
    await app?.close();
  });
  it("registers a bcrypt account, issues JWT and grants the registration coupon", async () => {
    expect(token).toBeTruthy();
    expect(await db.getRepository(MemberCoupon).countBy({ memberId })).toBe(1);
    await request(app.getHttpServer())
      .post("/api/auth/login")
      .send({ loginId: "tester", password: "wrong" })
      .expect(401);
  });
  it("keeps item response fields and validates pagination", async () => {
    const result = await request(app.getHttpServer())
      .get("/api/items/list")
      .expect(200);
    expect(result.body.itemSummaryResponseList[0]).toMatchObject({
      id: itemId,
      inWishList: false,
      rating: 0,
      categoryName: "와인",
    });
    await request(app.getHttpServer())
      .get("/api/items/list?size=-1")
      .expect(400);
    await request(app.getHttpServer())
      .get("/api/items/similar-price")
      .expect(400);
  });
  it("requires authentication and rejects invalid JWTs on public routes", async () => {
    await request(app.getHttpServer()).get("/api/members/carts").expect(401);
    await request(app.getHttpServer())
      .get("/api/items/list")
      .set("Authorization", "Bearer forged")
      .expect(401);
  });
  it("supports wishlist personalization and removal", async () => {
    await request(app.getHttpServer())
      .post("/api/members/wish-item")
      .auth(token, { type: "bearer" })
      .send({ itemId })
      .expect(200);
    const result = await request(app.getHttpServer())
      .get("/api/items/list")
      .auth(token, { type: "bearer" })
      .expect(200);
    expect(result.body.itemSummaryResponseList[0].inWishList).toBe(true);
    const anonymous = await request(app.getHttpServer())
      .get("/api/items/list")
      .expect(200);
    expect(anonymous.body.itemSummaryResponseList[0].inWishList).toBe(false);
    const wishes = await request(app.getHttpServer())
      .get("/api/members/wish-item")
      .auth(token, { type: "bearer" })
      .expect(200);
    expect(wishes.body.wishlist).toEqual(
      expect.arrayContaining([expect.objectContaining({ itemId })]),
    );
    await request(app.getHttpServer())
      .delete(`/api/members/wish-item?itemId=${itemId}`)
      .auth(token, { type: "bearer" })
      .expect(200);
    const removed = await request(app.getHttpServer())
      .get("/api/items/list")
      .auth(token, { type: "bearer" })
      .expect(200);
    expect(removed.body.itemSummaryResponseList[0].inWishList).toBe(false);
  });
  it("rejects nonpositive quantities and duplicate order items before persistence", async () => {
    await request(app.getHttpServer())
      .post("/api/members/carts")
      .auth(token, { type: "bearer" })
      .send({ itemId, amount: -1 })
      .expect(400);
    await request(app.getHttpServer())
      .post("/api/orders")
      .auth(token, { type: "bearer" })
      .send({
        phoneNumber: "010",
        address: "서울",
        recipient: "홍길동",
        totalPrice: 40000,
        orderItems: [
          { itemId, quantity: 1 },
          { itemId, quantity: 1 },
        ],
      })
      .expect(400);
    expect(await db.getRepository(Order).count()).toBe(0);
  });
  it("validates multipart JSON and hides secret inquiry content", async () => {
    await request(app.getHttpServer())
      .post("/api/inquiries")
      .auth(token, { type: "bearer" })
      .field("inquiryRequest", "{broken")
      .expect(400);
    const created = await request(app.getHttpServer())
      .post("/api/inquiries")
      .auth(token, { type: "bearer" })
      .field(
        "inquiryRequest",
        JSON.stringify({
          itemId,
          title: "비밀 제목",
          content: "비밀 내용",
          isSecret: true,
        }),
      )
      .expect(200);
    const multipartValues = await request(app.getHttpServer())
      .post("/api/inquiries")
      .auth(token, { type: "bearer" })
      .field(
        "inquiryRequest",
        JSON.stringify({
          itemId: String(itemId),
          title: "문자열 값 문의",
          content: "multipart 요청 값",
          secret: "false",
        }),
      )
      .expect(200);
    expect(multipartValues.body.inquiryId).toEqual(expect.any(Number));
    const list = await request(app.getHttpServer())
      .get(`/api/inquiries/list?itemId=${itemId}`)
      .expect(200);
    expect(list.body).toEqual(expect.arrayContaining([expect.objectContaining({
      title: "비밀글입니다.",
      memberName: "홍*동",
      secretInquiry: true,
      thisOwnInquiry: false,
    })]));
    const detail = await request(app.getHttpServer())
      .get(`/api/inquiries/${created.body.inquiryId}`)
      .auth(token, { type: "bearer" })
      .expect(200);
    expect(detail.body.content).toBe("비밀 내용");
  });
  it("does not expose another member order", async () => {
    const other = await db
      .getRepository(Member)
      .save({ name: "다른회원", nickname: "다른회원", phoneNumber: "010" });
    const order = await db
      .getRepository(Order)
      .save({
        memberId: other.id,
        status: "COMPLETE",
        comment: "",
        phoneNumber: "010",
        address: "서울",
        recipient: "다른회원",
        price: 20000,
        discountPrice: 0,
        deliveryFee: 3000,
        paymentMethod: "CREDIT_CARD",
      });
    await db
      .getRepository(OrderItem)
      .save({ orderId: order.id, itemId, amount: 1 });
    await request(app.getHttpServer())
      .get(`/api/orders/${order.id}`)
      .auth(token, { type: "bearer" })
      .expect(403);
  });
  const mysqlTest = process.env.TEST_MYSQL === "1" ? it : it.skip;
  mysqlTest(
    "rolls back mismatched orders and consumes coupons exactly once",
    async () => {
      const body = {
        phoneNumber: "010",
        address: "서울",
        recipient: "홍길동",
        totalPrice: 1,
        couponId: 1,
        orderItems: [{ itemId, quantity: 2 }],
      };
      await request(app.getHttpServer())
        .post("/api/orders")
        .auth(token, { type: "bearer" })
        .send(body)
        .expect(400);
      expect(await db.getRepository(MemberCoupon).countBy({ memberId })).toBe(
        1,
      );
      expect(
        (await db.getRepository(Item).findOneByOrFail({ id: itemId }))
          .orderCount,
      ).toBe(0);
      const created = await request(app.getHttpServer())
        .post("/api/orders")
        .auth(token, { type: "bearer" })
        .send({ ...body, totalPrice: 36000 })
        .expect(200);
      const detail = await request(app.getHttpServer())
        .get(`/api/orders/${created.body}`)
        .auth(token, { type: "bearer" })
        .expect(200);
      expect(detail.body).toMatchObject({
        price: 36000,
        discountPrice: 4000,
        deliveryFee: 3000,
      });
      expect(await db.getRepository(MemberCoupon).countBy({ memberId })).toBe(
        0,
      );
      await request(app.getHttpServer())
        .post("/api/orders")
        .auth(token, { type: "bearer" })
        .send({ ...body, totalPrice: 36000 })
        .expect(400);
      const review = await request(app.getHttpServer())
        .post("/api/reviews")
        .auth(token, { type: "bearer" })
        .field(
          "reviewCreationRequest",
          JSON.stringify({
            itemId,
            orderId: created.body,
            title: "리뷰",
            content: "좋아요",
            rating: 5,
          }),
        )
        .expect(200);
      await request(app.getHttpServer())
        .put(`/api/reviews/${review.body.reviewId}`)
        .auth(token, { type: "bearer" })
        .send({ content: "수정", rating: 3 })
        .expect(200);
      expect(
        (await db.getRepository(Item).findOneByOrFail({ id: itemId }))
          .ratingSum,
      ).toBe(3);
      await request(app.getHttpServer())
        .delete(`/api/reviews/${review.body.reviewId}`)
        .auth(token, { type: "bearer" })
        .expect(200);
      await request(app.getHttpServer())
        .delete(`/api/reviews/${review.body.reviewId}`)
        .auth(token, { type: "bearer" })
        .expect(400);
      expect(
        (await db.getRepository(Item).findOneByOrFail({ id: itemId }))
          .ratingCount,
      ).toBe(0);
    },
  );
  mysqlTest(
    "merges cart quantities and enforces a single default address",
    async () => {
      for (const amount of [1, 2])
        await request(app.getHttpServer())
          .post("/api/members/carts")
          .auth(token, { type: "bearer" })
          .send({ itemId, amount })
          .expect(200);
      const carts = await request(app.getHttpServer())
        .get("/api/members/carts")
        .auth(token, { type: "bearer" })
        .expect(200);
      expect(carts.body[0].amount).toBe(3);
      const address = {
        addressAlias: "집",
        recipient: "홍길동",
        address: "서울",
        detailAddress: "101호",
        phoneNumber: "010",
        default: true,
      };
      for (let i = 0; i < 2; i++)
        await request(app.getHttpServer())
          .post("/api/members/address")
          .auth(token, { type: "bearer" })
          .send(address)
          .expect(200);
      const addresses = await request(app.getHttpServer())
        .get("/api/members/address")
        .auth(token, { type: "bearer" })
        .expect(200);
      expect(
        addresses.body.filter((a: { default: boolean }) => a.default),
      ).toHaveLength(1);
    },
  );
  it("rejects invalid review ratings and disables tokens after withdrawal", async () => {
    await request(app.getHttpServer())
      .post("/api/reviews")
      .auth(token, { type: "bearer" })
      .field(
        "reviewCreationRequest",
        JSON.stringify({
          itemId,
          orderId: 1,
          title: "리뷰",
          content: "내용",
          rating: 6,
        }),
      )
      .expect(400);
    expect(await db.getRepository(Review).countBy({ isDeleted: false })).toBe(
      0,
    );
    await request(app.getHttpServer())
      .delete("/api/members/withdraw")
      .auth(token, { type: "bearer" })
      .expect(200);
    await request(app.getHttpServer())
      .get("/api/members/coupons")
      .auth(token, { type: "bearer" })
      .expect(401);
  });
});
