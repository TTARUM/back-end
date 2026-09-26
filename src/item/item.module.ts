import {
  Controller,
  Get,
  Injectable,
  Module,
  Param,
  ParseIntPipe,
  Query,
  Req,
  BadRequestException,
} from "@nestjs/common";
import {
  IsInt,
  IsOptional,
  IsString,
  Max,
  MaxLength,
  Min,
} from "class-validator";
import { Type } from "class-transformer";
import { DataSource, Between, Like, FindOptionsWhere } from "typeorm";
import { Category, Item, Wishlist } from "../database/entities";
import { PageDto, pagination, type UserRequest } from "../common/http";
import { Public } from "../auth/auth.module";
class ItemQuery extends PageDto {
  @IsOptional() @IsString() @MaxLength(100) query = "";
}
class PriceQuery extends PageDto {
  @Type(() => Number) @IsInt() @Min(0) price: number;
}
class PopularQuery {
  @Type(() => Number) @IsInt() @Min(1) @Max(100) number = 5;
}
@Injectable()
export class ItemService {
  constructor(private readonly db: DataSource) {}
  async get(id: number) {
    const item = await this.db.getRepository(Item).findOneBy({ id });
    if (!item) throw new BadRequestException("제품이 존재하지 않습니다.");
    return item;
  }
  async summary(item: Item, memberId?: number) {
    const category = await this.db
      .getRepository(Category)
      .findOneBy({ id: item.categoryId });
    return {
      id: item.id,
      categoryId: item.categoryId,
      categoryName: category?.name ?? "",
      name: item.name,
      price: item.price,
      rating: item.ratingCount
        ? Number(item.ratingSum) / Number(item.ratingCount)
        : 0,
      imageUrl: item.itemImageUrl,
      inWishList: memberId
        ? await this.db
            .getRepository(Wishlist)
            .existsBy({ memberId, itemId: item.id })
        : false,
      createdAt: item.createdAt,
      orderCount: Number(item.orderCount),
    };
  }
  async list(
    query: PageDto,
    memberId?: number,
    where: FindOptionsWhere<Item> = {},
    popular = false,
    defaultSize = 9,
  ) {
    const items = await this.db
      .getRepository(Item)
      .find({
        where,
        ...pagination(query, defaultSize),
        order: popular ? { orderCount: "DESC", id: "ASC" } : { id: "ASC" },
      });
    return {
      itemSummaryResponseList: await Promise.all(
        items.map((i) => this.summary(i, memberId)),
      ),
    };
  }
  async detail(id: number) {
    const i = await this.get(id);
    return {
      name: i.name,
      description: i.description,
      price: i.price,
      imageUrl: i.itemImageUrl,
      descriptionImageUrl: i.itemDescriptionImageUrl,
    };
  }
  async popular(number: number) {
    return (
      await this.db
        .getRepository(Item)
        .find({ take: number, order: { orderCount: "DESC", id: "ASC" } })
    ).map((i) => ({
      itemId: i.id,
      itemName: i.name,
      count: Number(i.orderCount),
    }));
  }
  async similar(q: PriceQuery, memberId?: number) {
    const result = await this.list(
      q,
      memberId,
      { price: Between(Math.max(0, q.price - 10000), q.price + 10000) },
      false,
      7,
    );
    return {
      itemSummaryList: result.itemSummaryResponseList.map((i) => ({
        itemId: i.id,
        itemName: i.name,
        price: i.price,
        imageUrl: i.imageUrl,
        inWishList: i.inWishList,
      })),
    };
  }
}
@Public()
@Controller("items")
class ItemController {
  constructor(private readonly items: ItemService) {}
  @Get("list") list(@Query() q: ItemQuery, @Req() req: UserRequest) {
    return this.items.list(q, req.memberId, { name: Like(`%${q.query}%`) });
  }
  @Get("popular-list") popular(@Query() q: PopularQuery) {
    return this.items.popular(q.number);
  }
  @Get("similar-price") similar(
    @Query() q: PriceQuery,
    @Req() req: UserRequest,
  ) {
    return this.items.similar(q, req.memberId);
  }
  @Get("popular-in-category/:id") popularCategory(
    @Param("id", ParseIntPipe) id: number,
    @Query() q: PageDto,
    @Req() r: UserRequest,
  ) {
    return this.items.list(q, r.memberId, { categoryId: id }, true, 7);
  }
  @Get("category/:id") category(
    @Param("id", ParseIntPipe) id: number,
    @Query() q: PageDto,
    @Req() r: UserRequest,
  ) {
    return this.items.list(q, r.memberId, { categoryId: id });
  }
  @Get(":id") detail(@Param("id", ParseIntPipe) id: number) {
    return this.items.detail(id);
  }
}
@Module({
  controllers: [ItemController],
  providers: [ItemService],
  exports: [ItemService],
})
export class ItemModule {}
