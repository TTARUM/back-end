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
} from "@nestjs/common";
import { Type } from "class-transformer";
import {
  ArrayMaxSize,
  ArrayNotEmpty,
  IsArray,
  IsInt,
  IsOptional,
  IsString,
  Length,
  Max,
  MaxLength,
  Min,
  ValidateNested,
} from "class-validator";
import { DataSource, In } from "typeorm";
import {
  Coupon,
  Item,
  Member,
  MemberCoupon,
  Order,
  OrderItem,
} from "../database/entities";
import { PageDto, pagination, type UserRequest } from "../common/http";
class OrderItemDto {
  @IsInt() @Min(1) itemId: number;
  @IsInt() @Min(1) @Max(1000000) quantity: number;
}
export class OrderDto {
  @IsOptional() @IsString() @MaxLength(100) comment = "";
  @IsString() @Length(1, 15) phoneNumber: string;
  @IsString() @Length(1, 100) address: string;
  @IsString() @Length(1, 20) recipient: string;
  @IsArray()
  @ArrayNotEmpty()
  @ArrayMaxSize(100)
  @ValidateNested({ each: true })
  @Type(() => OrderItemDto)
  orderItems: OrderItemDto[];
  @IsOptional() @IsInt() @Min(1) couponId?: number;
  @IsInt() @Min(0) @Max(2147483647) totalPrice: number;
}
export function discountFor(
  total: number,
  coupon: Pick<Coupon, "couponStrategy" | "value">,
) {
  if (
    coupon.value < 0 ||
    !["PERCENTAGE", "ABSOLUTE"].includes(coupon.couponStrategy)
  )
    throw new BadRequestException("유효하지 않은 쿠폰입니다.");
  return Math.min(
    total,
    coupon.couponStrategy === "PERCENTAGE"
      ? Math.floor((total * coupon.value) / 100)
      : coupon.value,
  );
}
@Injectable()
export class OrderService {
  constructor(private readonly db: DataSource) {}
  async create(memberId: number, dto: OrderDto) {
    const ids = dto.orderItems.map((i) => i.itemId);
    if (new Set(ids).size !== ids.length)
      throw new BadRequestException("중복된 주문 상품입니다.");
    return this.db.transaction(async (em) => {
      await em.findOneOrFail(Member, {
        where: { id: memberId },
        lock: { mode: "pessimistic_write" },
      });
      const items = await em.find(Item, {
        where: { id: In(ids) },
        order: { id: "ASC" },
        lock: { mode: "pessimistic_write" },
      });
      if (items.length !== ids.length)
        throw new BadRequestException("주문 상품이 존재하지 않습니다.");
      const quantities = new Map(
        dto.orderItems.map((i) => [i.itemId, i.quantity]),
      );
      const subtotal = items.reduce(
        (sum, i) => sum + i.price * quantities.get(i.id)!,
        0,
      );
      if (!Number.isSafeInteger(subtotal) || subtotal > 2147483647)
        throw new BadRequestException("주문 금액 한도를 초과했습니다.");
      let discountPrice = 0;
      let owned: MemberCoupon | null = null;
      if (dto.couponId) {
        owned = await em.findOneBy(MemberCoupon, {
          memberId,
          couponId: dto.couponId,
        });
        const coupon = await em.findOneBy(Coupon, { id: dto.couponId });
        if (!owned || !coupon)
          throw new BadRequestException("쿠폰이 존재하지 않습니다.");
        discountPrice = discountFor(subtotal, coupon);
      }
      const price = subtotal - discountPrice;
      if (price !== dto.totalPrice)
        throw new BadRequestException("주문 금액이 일치하지 않습니다.");
      const order = await em.save(
        Order,
        em.create(Order, {
          memberId,
          status: "COMPLETE",
          comment: dto.comment ?? "",
          phoneNumber: dto.phoneNumber,
          address: dto.address,
          recipient: dto.recipient,
          price,
          discountPrice,
          deliveryFee: price >= 100000 ? 0 : 3000,
          paymentMethod: "CREDIT_CARD",
        }),
      );
      for (const item of items) {
        const amount = quantities.get(item.id)!;
        await em.insert(OrderItem, {
          orderId: order.id,
          itemId: item.id,
          amount,
        });
        await em.increment(Item, { id: item.id }, "orderCount", amount);
      }
      if (owned) await em.delete(MemberCoupon, owned.id);
      return order.id;
    });
  }
  async response(order: Order) {
    const lines = await this.db
      .getRepository(OrderItem)
      .findBy({ orderId: order.id });
    const items = await this.db
      .getRepository(Item)
      .findBy({ id: In(lines.map((l) => l.itemId)) });
    return {
      orderId: order.id,
      orderStatus: order.status,
      orderDate: order.createdAt,
      price: Number(order.price),
      discountPrice: Number(order.discountPrice),
      recipient: order.recipient,
      phoneNumber: order.phoneNumber,
      address: order.address,
      comment: order.comment,
      paymentMethod: order.paymentMethod,
      deliveryFee: order.deliveryFee,
      orderItemSummaryList: lines.map((l) => {
        const i = items.find((i) => i.id === l.itemId)!;
        return {
          itemId: i.id,
          itemImageUrl: i.itemImageUrl,
          itemName: i.name,
          itemPrice: i.price,
          amount: Number(l.amount),
        };
      }),
    };
  }
  async detail(memberId: number, id: number) {
    const order = await this.db.getRepository(Order).findOneBy({ id });
    if (!order) throw new BadRequestException("주문을 찾을 수 없습니다.");
    if (order.memberId !== memberId) throw new ForbiddenException();
    return this.response(order);
  }
  async list(memberId: number, q: PageDto) {
    return Promise.all(
      (
        await this.db
          .getRepository(Order)
          .find({
            where: { memberId },
            ...pagination(q, 5),
            order: { id: "DESC" },
          })
      ).map((o) => this.response(o)),
    );
  }
}
@Controller("orders")
export class OrderController {
  constructor(private readonly orders: OrderService) {}
  @Post() @HttpCode(200) create(@Req() r: UserRequest, @Body() d: OrderDto) {
    return this.orders.create(r.memberId!, d);
  }
  @Get("list") list(@Req() r: UserRequest, @Query() q: PageDto) {
    return this.orders.list(r.memberId!, q);
  }
  @Get(":id") detail(
    @Req() r: UserRequest,
    @Param("id", ParseIntPipe) id: number,
  ) {
    return this.orders.detail(r.memberId!, id);
  }
}
@Module({ controllers: [OrderController], providers: [OrderService] })
export class OrderModule {}
