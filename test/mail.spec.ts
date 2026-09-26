import { ConfigService } from "@nestjs/config";
import { Logger, ServiceUnavailableException } from "@nestjs/common";
import { DataSource } from "typeorm";
import Redis from "ioredis";
import { createTransport } from "nodemailer";
import { MailService } from "../src/member/mail.service";

jest.mock("ioredis");
jest.mock("nodemailer", () => ({ createTransport: jest.fn() }));

describe("Email verification storage failures", () => {
  let service: MailService;
  let redis: {
    on: jest.Mock;
    set: jest.Mock;
    get: jest.Mock;
    eval: jest.Mock;
    disconnect: jest.Mock;
  };
  beforeEach(() => {
    jest.clearAllMocks();
    jest.spyOn(Logger.prototype, "error").mockImplementation(() => undefined);
    redis = {
      on: jest.fn(),
      set: jest.fn(),
      get: jest.fn(),
      eval: jest.fn(),
      disconnect: jest.fn(),
    };
    (Redis as unknown as jest.Mock).mockImplementation(() => redis);
    const db = {
      getRepository: () => ({ existsBy: jest.fn().mockResolvedValue(false) }),
    };
    const config = new ConfigService({
      MAIL_USERNAME: "sender@example.com",
      MAIL_PASSWORD: "test-only",
    });
    service = new MailService(db as unknown as DataSource, config);
  });
  afterEach(() => {
    service.onModuleDestroy();
    jest.restoreAllMocks();
  });

  it("returns 503 and does not attempt SMTP when Redis is unavailable", async () => {
    redis.set.mockRejectedValue(new Error("ECONNREFUSED"));
    await expect(service.send("recipient@example.com")).rejects.toMatchObject({
      status: 503,
    });
    expect(createTransport).not.toHaveBeenCalled();
  });
  it("reports unavailable storage for code checks", async () => {
    redis.eval.mockRejectedValue(new Error("NOAUTH"));
    await expect(
      service.check("recipient@example.com", "123456", "register"),
    ).rejects.toBeInstanceOf(ServiceUnavailableException);
  });
  it("reports unavailable storage for account recovery", async () => {
    redis.get.mockRejectedValue(new Error("ECONNREFUSED"));
    await expect(
      service.find({
        name: "Test",
        email: "recipient@example.com",
        verificationCode: "123456",
        sessionId: "session",
      }),
    ).rejects.toMatchObject({ status: 503 });
  });
  it("keeps resend cooldown errors as 400", async () => {
    redis.set.mockResolvedValue(null);
    await expect(service.send("recipient@example.com")).rejects.toMatchObject({
      status: 400,
    });
  });
});
