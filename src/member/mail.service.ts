import {
  BadRequestException,
  Injectable,
  OnModuleDestroy,
  ServiceUnavailableException,
} from "@nestjs/common";
import { ConfigService } from "@nestjs/config";
import { DataSource } from "typeorm";
import Redis from "ioredis";
import { createTransport } from "nodemailer";
import { randomInt, randomUUID, createHash } from "node:crypto";
import { Member, NormalMember, OauthMember } from "../database/entities";
import { FindIdDto } from "./member.dto";
type Verification = {
  code: string;
  uuid: string;
  valid: boolean;
  name?: string;
  attempts: number;
};
@Injectable()
export class MailService implements OnModuleDestroy {
  private readonly redis: Redis;
  constructor(
    private readonly db: DataSource,
    private readonly config: ConfigService,
  ) {
    this.redis = new Redis({
      host: config.get("REDIS_HOST", "localhost"),
      port: Number(config.get("REDIS_PORT", 6379)),
      password: config.get("REDIS_PASSWORD") || undefined,
      lazyConnect: true,
      maxRetriesPerRequest: 1,
    });
    this.redis.on("error", () => undefined);
  }
  onModuleDestroy() {
    this.redis.disconnect();
  }
  private key(email: string, purpose: string) {
    return `mail:${purpose}:${createHash("sha256").update(email).digest("hex")}`;
  }
  async send(email: string, name?: string) {
    const purpose = name === undefined ? "register" : "find";
    if (purpose === "register") {
      if (
        (await this.db.getRepository(NormalMember).existsBy({ email })) ||
        (await this.db.getRepository(OauthMember).existsBy({ email }))
      )
        throw new BadRequestException("중복된 이메일입니다.");
    } else {
      const account = await this.db
        .getRepository(NormalMember)
        .findOneBy({ email });
      if (
        !account ||
        !(await this.db
          .getRepository(Member)
          .existsBy({ id: account.memberId, name, isDeleted: false }))
      )
        throw new BadRequestException("회원 정보가 일치하지 않습니다.");
    }
    if (!this.config.get("MAIL_USERNAME") || !this.config.get("MAIL_PASSWORD"))
      throw new ServiceUnavailableException("메일 설정이 필요합니다.");
    const key = this.key(email, purpose);
    if (!(await this.redis.set(`${key}:cooldown`, "1", "EX", 60, "NX")))
      throw new BadRequestException("잠시 후 다시 요청해주세요.");
    const code = String(randomInt(0, 1000000)).padStart(6, "0");
    const transporter = createTransport({
      host: this.config.get("MAIL_HOST", "smtp.gmail.com"),
      port: Number(this.config.get("MAIL_PORT", 587)),
      secure: Number(this.config.get("MAIL_PORT", 587)) === 465,
      auth: {
        user: this.config.get("MAIL_USERNAME"),
        pass: this.config.get("MAIL_PASSWORD"),
      },
      connectionTimeout: 5000,
    });
    await this.redis.set(
      key,
      JSON.stringify({
        code,
        uuid: randomUUID(),
        valid: false,
        name,
        attempts: 0,
      }),
      "EX",
      180,
    );
    try {
      await transporter.sendMail({
        from: this.config.get("MAIL_USERNAME"),
        to: email,
        subject: "[TTARUM] 이메일 인증",
        text: `인증 번호: ${code}\n3분 이내에 입력해주세요.`,
      });
    } catch {
      await this.redis.del(key);
      throw new ServiceUnavailableException("메일 전송에 실패했습니다.");
    }
    return {};
  }
  async check(email: string, code: string, purpose: "register" | "find") {
    const key = this.key(email, purpose);
    // Atomic verification prevents concurrent requests from resetting attempt limits or TTL.
    const result = await this.redis.eval(
      `
      local raw = redis.call('GET', KEYS[1])
      if not raw then return nil end
      local v = cjson.decode(raw)
      if v.attempts >= 5 then return nil end
      v.attempts = v.attempts + 1
      if v.code == ARGV[1] then v.valid = true end
      redis.call('SET', KEYS[1], cjson.encode(v), 'KEEPTTL')
      if v.code ~= ARGV[1] then return nil end
      return cjson.encode(v)
    `,
      1,
      key,
      code,
    );
    if (typeof result !== "string")
      throw new BadRequestException("인증 번호가 잘못되었거나 만료되었습니다.");
    const value: Verification = JSON.parse(result);
    return purpose === "find" ? { uuid: value.uuid } : {};
  }
  async find(dto: FindIdDto) {
    const key = this.key(dto.email, "find");
    const raw = await this.redis.get(key);
    const value: Verification | undefined = raw ? JSON.parse(raw) : undefined;
    if (
      !value?.valid ||
      value.code !== dto.verificationCode ||
      value.uuid !== dto.sessionId ||
      value.name !== dto.name
    )
      throw new BadRequestException("인증 정보가 올바르지 않습니다.");
    const account = await this.db
      .getRepository(NormalMember)
      .findOneBy({ email: dto.email });
    if (!account) throw new BadRequestException("회원을 찾을 수 없습니다.");
    await this.redis.del(key);
    return { email: account.loginId };
  }
}
