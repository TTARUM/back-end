import {
  BadRequestException,
  Global,
  Injectable,
  Logger,
  Module,
  ServiceUnavailableException,
} from "@nestjs/common";
import { ConfigService } from "@nestjs/config";
import {
  DeleteObjectCommand,
  PutObjectCommand,
  S3Client,
} from "@aws-sdk/client-s3";
import { randomUUID } from "node:crypto";
export const uploadOptions = {
  limits: { fileSize: 5 * 1024 * 1024, files: 10, fields: 10 },
};
@Injectable()
export class StorageService {
  private readonly client: S3Client;
  private readonly logger = new Logger(StorageService.name);
  constructor(private readonly config: ConfigService) {
    const accessKeyId = config.get<string>("AWS_ACCESS_KEY");
    const secretAccessKey = config.get<string>("AWS_SECRET_KEY");
    this.client = new S3Client({
      region: config.get("AWS_REGION", "ap-northeast-2"),
      ...(accessKeyId && secretAccessKey
        ? { credentials: { accessKeyId, secretAccessKey } }
        : {}),
    });
  }
  async upload(files: Express.Multer.File[] = []) {
    const uploaded: { key: string; url: string }[] = [];
    try {
      for (const file of files) {
        const b = file.buffer;
        const ext = b
          ?.subarray(0, 8)
          .equals(Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]))
          ? "png"
          : b?.[0] === 255 && b?.[1] === 216 && b?.[2] === 255
            ? "jpg"
            : b
                  ?.subarray(0, 6)
                  .toString()
                  .match(/^GIF8[79]a$/)
              ? "gif"
              : undefined;
        if (!ext || file.size > 5 * 1024 * 1024)
          throw new BadRequestException(
            "5MB 이하 PNG, JPEG, GIF 이미지만 가능합니다.",
          );
        const bucket = this.config.get<string>("S3_BUCKET");
        if (!bucket)
          throw new ServiceUnavailableException("S3_BUCKET 설정이 필요합니다.");
        const key = randomUUID() + "." + ext;
        const base =
          this.config.get<string>("S3_PUBLIC_URL") ||
          `https://${bucket}.s3.${this.config.get("AWS_REGION", "ap-northeast-2")}.amazonaws.com`;
        const url = `${base.replace(/\/$/, "")}/${key}`;
        if (url.length > 100)
          throw new BadRequestException(
            "기존 DB 이미지 URL 길이 제한(100자)을 초과합니다. S3_PUBLIC_URL을 설정하세요.",
          );
        await this.client.send(
          new PutObjectCommand({
            Bucket: bucket,
            Key: key,
            Body: b,
            ContentType: `image/${ext === "jpg" ? "jpeg" : ext}`,
          }),
        );
        uploaded.push({ key, url });
      }
      return uploaded;
    } catch (error) {
      await this.cleanup(uploaded);
      throw error;
    }
  }
  async cleanup(files: { key: string }[]) {
    await Promise.all(
      files.map(async (file) => {
        try {
          await this.client.send(
            new DeleteObjectCommand({
              Bucket: this.config.get("S3_BUCKET"),
              Key: file.key,
            }),
          );
        } catch (error) {
          this.logger.error(`S3 cleanup failed: ${file.key}`, error);
        }
      }),
    );
  }
}
@Global()
@Module({ providers: [StorageService], exports: [StorageService] })
export class StorageModule {}
