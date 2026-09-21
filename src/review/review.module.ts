import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  ForbiddenException,
  Get,
  HttpCode,
  Injectable,
  Module,
  Param,
  ParseIntPipe,
  Post,
  Put,
  Query,
  Req,
  UploadedFiles,
  UseInterceptors,
} from "@nestjs/common";
import { FilesInterceptor } from "@nestjs/platform-express";
import { Type } from "class-transformer";
import { IsInt, IsString, Length, Max, Min } from "class-validator";
import { DataSource, In, EntityManager } from "typeorm";
import {
  Item,
  Member,
  Order,
  OrderItem,
  Review,
  ReviewImage,
} from "../database/entities";
import { multipartDto, PageDto, pagination, type UserRequest } from "../common/http";
import { StorageService, uploadOptions } from "../storage/storage.module";
export class ReviewUpdateDto {
  @IsString() @Length(1, 500) content: string;
  @IsInt() @Min(0) @Max(5) rating: number;
}
export class ReviewCreateDto extends ReviewUpdateDto {
  @IsInt() @Min(1) orderId: number;
  @IsInt() @Min(1) itemId: number;
  @IsString() @Length(1, 255) title: string;
}
class ReviewQuery extends PageDto {
  @Type(() => Number) @IsInt() @Min(1) itemId: number;
}
@Injectable()
export class ReviewService {
  constructor(
    private readonly db: DataSource,
    private readonly storage: StorageService,
  ) {}
  private async owned(
    em: EntityManager,
    id: number,
    memberId: number,
    lock = false,
  ) {
    const review = await em.findOne(Review, {
      where: { id, isDeleted: false },
      ...(lock ? { lock: { mode: "pessimistic_write" as const } } : {}),
    });
    if (!review) throw new BadRequestException("리뷰를 찾을 수 없습니다.");
    if (review.memberId !== memberId)
      throw new ForbiddenException("사용자의 리뷰가 아닙니다.");
    return review;
  }
  async create(
    memberId: number,
    dto: ReviewCreateDto,
    files: Express.Multer.File[],
  ) {
    const uploaded = await this.storage.upload(files);
    try {
      return await this.db.transaction(async (em) => {
        const order = await em.findOne(Order, {
          where: { id: dto.orderId },
          lock: { mode: "pessimistic_write" },
        });
        if (!order || order.memberId !== memberId)
          throw new ForbiddenException("사용자의 주문이 아닙니다.");
        if (
          !(await em.existsBy(OrderItem, {
            orderId: dto.orderId,
            itemId: dto.itemId,
          }))
        )
          throw new BadRequestException("주문에 포함되지 않은 제품입니다.");
        if (
          await em.existsBy(Review, {
            orderId: dto.orderId,
            itemId: dto.itemId,
          })
        )
          throw new BadRequestException("이미 작성한 리뷰가 있습니다.");
        const review = await em.save(
          Review,
          em.create(Review, {
            memberId,
            orderId: dto.orderId,
            itemId: dto.itemId,
            title: dto.title,
            content: dto.content,
            star: dto.rating,
          }),
        );
        await em.increment(Item, { id: dto.itemId }, "ratingSum", dto.rating);
        await em.increment(Item, { id: dto.itemId }, "ratingCount", 1);
        for (const [index, file] of uploaded.entries())
          await em.insert(ReviewImage, {
            reviewId: review.id,
            fileUrl: file.url,
            order: index + 1,
          });
        return { reviewId: review.id };
      });
    } catch (e) {
      await this.storage.cleanup(uploaded);
      throw e;
    }
  }
  async update(memberId: number, id: number, dto?: ReviewUpdateDto) {
    await this.db.transaction(async (em) => {
      const review = await this.owned(em, id, memberId, true);
      await em.increment(
        Item,
        { id: review.itemId },
        "ratingSum",
        (dto?.rating ?? 0) - review.star,
      );
      if (dto)
        await em.update(Review, id, { content: dto.content, star: dto.rating });
      else {
        await em.increment(Item, { id: review.itemId }, "ratingCount", -1);
        await em.update(Review, id, { isDeleted: true });
      }
    });
    return {};
  }
  async list(q: PageDto, itemId?: number, memberId?: number) {
    if (itemId && !(await this.db.getRepository(Item).existsBy({ id: itemId })))
      throw new BadRequestException("제품이 존재하지 않습니다.");
    const reviews = await this.db
      .getRepository(Review)
      .find({
        where: { isDeleted: false, ...(itemId ? { itemId } : { memberId }) },
        ...pagination(q),
        order: { id: "DESC" },
      });
    const images = await this.db
      .getRepository(ReviewImage)
      .find({
        where: { reviewId: In(reviews.map((r) => r.id)) },
        order: { order: "ASC" },
      });
    const members = await this.db
      .getRepository(Member)
      .findBy({ id: In(reviews.map((r) => r.memberId)) });
    return reviews.map((r) => ({
      id: r.id,
      nickname: members.find((m) => m.id === r.memberId)?.nickname ?? "",
      content: r.content,
      rating: r.star,
      createdAt: r.createdAt,
      imageUrls: images
        .filter((i) => i.reviewId === r.id)
        .map((i) => ({ order: i.order, imageUrl: i.fileUrl })),
    }));
  }
  async forUpdate(memberId: number, id: number) {
    const review = await this.owned(this.db.manager, id, memberId);
    const item = await this.db
      .getRepository(Item)
      .findOneByOrFail({ id: review.itemId });
    const images = await this.db
      .getRepository(ReviewImage)
      .find({ where: { reviewId: id }, order: { order: "ASC" } });
    return {
      itemName: item.name,
      content: review.content,
      createdAt: review.createdAt,
      imageUrlList: images.map((i) => i.fileUrl),
    };
  }
}
@Controller("reviews")
export class ReviewController {
  constructor(private readonly reviews: ReviewService) {}
  @Get() list(@Query() q: ReviewQuery) {
    return this.reviews.list(q, q.itemId);
  }
  @Get("member") member(@Req() r: UserRequest, @Query() q: PageDto) {
    return this.reviews.list(q, undefined, r.memberId!);
  }
  @Post()
  @HttpCode(200)
  @UseInterceptors(FilesInterceptor("images", 10, uploadOptions))
  create(
    @Req() r: UserRequest,
    @Body("reviewCreationRequest") d: unknown,
    @UploadedFiles() files: Express.Multer.File[],
  ) {
    return this.reviews.create(
      r.memberId!,
      multipartDto(ReviewCreateDto, d),
      files ?? [],
    );
  }
  @Get(":id/update") forUpdate(
    @Req() r: UserRequest,
    @Param("id", ParseIntPipe) id: number,
  ) {
    return this.reviews.forUpdate(r.memberId!, id);
  }
  @Put(":id") update(
    @Req() r: UserRequest,
    @Param("id", ParseIntPipe) id: number,
    @Body() d: ReviewUpdateDto,
  ) {
    return this.reviews.update(r.memberId!, id, d);
  }
  @Delete(":id") delete(
    @Req() r: UserRequest,
    @Param("id", ParseIntPipe) id: number,
  ) {
    return this.reviews.update(r.memberId!, id);
  }
}
@Module({ controllers: [ReviewController], providers: [ReviewService] })
export class ReviewModule {}
