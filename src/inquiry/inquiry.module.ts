import {
  BadRequestException,
  Body,
  Controller,
  ForbiddenException,
  Get,
  HttpCode,
  Injectable,
  Module,
  Param,
  ParseIntPipe,
  Post,
  Query,
  Req,
  UploadedFiles,
  UseInterceptors,
} from "@nestjs/common";
import { FilesInterceptor } from "@nestjs/platform-express";
import { Transform, Type } from "class-transformer";
import {
  IsBoolean,
  IsInt,
  IsOptional,
  IsString,
  Length,
  Min,
} from "class-validator";
import { DataSource, In } from "typeorm";
import {
  Inquiry,
  InquiryAnswer,
  InquiryImage,
  Item,
  Member,
} from "../database/entities";
import { multipartDto, PageDto, pagination, type UserRequest } from "../common/http";
import { Public } from "../auth/auth.module";
import { StorageService, uploadOptions } from "../storage/storage.module";
export class InquiryDto {
  @IsString() @Length(1, 100) title: string;
  @IsString() @Length(1, 1000) content: string;
  @Type(() => Number) @IsInt() @Min(1) itemId: number;
  @IsOptional()
  @Transform(({ value }) => value === "true" ? true : value === "false" ? false : value)
  @IsBoolean()
  isSecret?: boolean;
  @IsOptional()
  @Transform(({ value }) => value === "true" ? true : value === "false" ? false : value)
  @IsBoolean()
  secret?: boolean;
}
class InquiryQuery extends PageDto {
  @Type(() => Number) @IsInt() @Min(1) itemId: number;
}
export function concealName(name: string) {
  return name.length < 2
    ? "*"
    : name.length === 2
      ? name[0] + "*"
      : name[0] + "*".repeat(name.length - 2) + name.at(-1);
}
@Injectable()
export class InquiryService {
  constructor(
    private readonly db: DataSource,
    private readonly storage: StorageService,
  ) {}
  async list(q: InquiryQuery, memberId?: number) {
    if (!(await this.db.getRepository(Item).existsBy({ id: q.itemId })))
      throw new BadRequestException("제품이 존재하지 않습니다.");
    const inquiries = await this.db
      .getRepository(Inquiry)
      .find({
        where: { itemId: q.itemId },
        ...pagination(q),
        order: { id: "DESC" },
      });
    const members = await this.db
      .getRepository(Member)
      .findBy({ id: In(inquiries.map((i) => i.memberId)) });
    return inquiries.map((i) => ({
      id: i.id,
      title: i.isSecret && i.memberId !== memberId ? "비밀글입니다." : i.title,
      secretInquiry: i.isSecret,
      thisOwnInquiry: i.memberId === memberId,
      hasAnswer: i.existAnswer,
      memberName: concealName(
        members.find((m) => m.id === i.memberId)?.name ?? "",
      ),
      createdAt: i.createdAt,
    }));
  }
  async detail(id: number, memberId?: number) {
    const inquiry = await this.db.getRepository(Inquiry).findOneBy({ id });
    if (!inquiry) throw new BadRequestException("문의글이 존재하지 않습니다.");
    if (inquiry.isSecret && inquiry.memberId !== memberId)
      throw new ForbiddenException("비밀글입니다.");
    const answer = inquiry.existAnswer
      ? await this.db.getRepository(InquiryAnswer).findOneBy({ inquiryId: id })
      : null;
    const images = await this.db
      .getRepository(InquiryImage)
      .find({ where: { inquiryId: id }, order: { id: "ASC" } });
    return {
      title: inquiry.title,
      content: inquiry.content,
      imageUrls: images.map((i) => ({ imageUrl: i.fileUrl })),
      inquiryAnswer: {
        content: answer?.content ?? "답변이 존재하지 않습니다.",
      },
    };
  }
  async create(
    memberId: number,
    dto: InquiryDto,
    files: Express.Multer.File[],
  ) {
    if (!dto.title.trim() || !dto.content.trim())
      throw new BadRequestException("제목과 내용이 필요합니다.");
    if (!(await this.db.getRepository(Item).existsBy({ id: dto.itemId })))
      throw new BadRequestException("제품이 존재하지 않습니다.");
    const uploaded = await this.storage.upload(files);
    try {
      return await this.db.transaction(async (em) => {
        const inquiry = await em.save(
          Inquiry,
          em.create(Inquiry, {
            memberId,
            itemId: dto.itemId,
            title: dto.title,
            content: dto.content,
            isSecret: dto.isSecret ?? dto.secret ?? false,
          }),
        );
        for (const file of uploaded)
          await em.insert(InquiryImage, {
            inquiryId: inquiry.id,
            fileUrl: file.url,
          });
        return { inquiryId: inquiry.id };
      });
    } catch (e) {
      await this.storage.cleanup(uploaded);
      throw e;
    }
  }
}
@Controller("inquiries")
export class InquiryController {
  constructor(private readonly inquiries: InquiryService) {}
  @Public() @Get("list") list(@Req() r: UserRequest, @Query() q: InquiryQuery) {
    return this.inquiries.list(q, r.memberId);
  }
  @Get(":id") detail(
    @Req() r: UserRequest,
    @Param("id", ParseIntPipe) id: number,
  ) {
    return this.inquiries.detail(id, r.memberId);
  }
  @Post()
  @HttpCode(200)
  @UseInterceptors(FilesInterceptor("images", 10, uploadOptions))
  create(
    @Req() r: UserRequest,
    @Body("inquiryRequest") d: unknown,
    @UploadedFiles() files: Express.Multer.File[],
  ) {
    return this.inquiries.create(
      r.memberId!,
      multipartDto(InquiryDto, d),
      files ?? [],
    );
  }
}
@Module({ controllers: [InquiryController], providers: [InquiryService] })
export class InquiryModule {}
