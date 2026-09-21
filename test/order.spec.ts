import { DataSource } from "typeorm";
import {
  Coupon,
  Item,
  MemberCoupon,
  Order,
  OrderItem,
} from "../src/database/entities";
import { discountFor, OrderDto, OrderService } from "../src/order/order.module";
describe("Order pricing and transaction boundaries", () => {
  const dto: OrderDto = {
    comment: "",
    phoneNumber: "010",
    address: "서울",
    recipient: "구매자",
    totalPrice: 36000,
    couponId: 1,
    orderItems: [{ itemId: 1, quantity: 2 }],
  };
  function setup() {
    const em = {
      findOneOrFail: jest.fn().mockResolvedValue({ id: 7 }),
      find: jest.fn().mockResolvedValue([{ id: 1, price: 20000 }]),
      findOneBy: jest.fn(async (entity: unknown) =>
        entity === Coupon
          ? { id: 1, couponStrategy: "PERCENTAGE", value: 10 }
          : { id: 42 },
      ),
      create: jest.fn((_entity: unknown, value: object) => value),
      save: jest.fn(async (_entity: unknown, value: object) => ({
        id: 9,
        ...value,
      })),
      insert: jest.fn(),
      increment: jest.fn(),
      delete: jest.fn(),
    };
    const transaction = jest.fn(async (callback) => callback(em));
    return {
      em,
      transaction,
      service: new OrderService({ transaction } as unknown as DataSource),
    };
  }
  it("rejects client price tampering before writing or consuming a coupon", async () => {
    const { service, em } = setup();
    await expect(service.create(7, { ...dto, totalPrice: 1 })).rejects.toThrow(
      "주문 금액이 일치하지 않습니다.",
    );
    expect(em.save).not.toHaveBeenCalled();
    expect(em.delete).not.toHaveBeenCalled();
    expect(em.increment).not.toHaveBeenCalled();
  });
  it("writes order, quantities, sale counts and coupon consumption inside the same transaction", async () => {
    const { service, em, transaction } = setup();
    await expect(service.create(7, dto)).resolves.toBe(9);
    expect(transaction).toHaveBeenCalledTimes(1);
    expect(em.save).toHaveBeenCalledWith(
      Order,
      expect.objectContaining({
        price: 36000,
        discountPrice: 4000,
        deliveryFee: 3000,
      }),
    );
    expect(em.insert).toHaveBeenCalledWith(OrderItem, {
      orderId: 9,
      itemId: 1,
      amount: 2,
    });
    expect(em.increment).toHaveBeenCalledWith(Item, { id: 1 }, "orderCount", 2);
    expect(em.delete).toHaveBeenCalledWith(MemberCoupon, 42);
  });
  it("rejects unowned coupons and unknown products", async () => {
    const first = setup();
    first.em.findOneBy.mockResolvedValue(null as never);
    await expect(first.service.create(7, dto)).rejects.toThrow(
      "쿠폰이 존재하지 않습니다.",
    );
    const second = setup();
    second.em.find.mockResolvedValue([]);
    await expect(second.service.create(7, dto)).rejects.toThrow(
      "주문 상품이 존재하지 않습니다.",
    );
  });
  it("waives delivery at 100000 after discount", async () => {
    const { service, em } = setup();
    await service.create(7, {
      ...dto,
      couponId: undefined,
      totalPrice: 100000,
      orderItems: [{ itemId: 1, quantity: 5 }],
    });
    expect(em.save).toHaveBeenCalledWith(
      Order,
      expect.objectContaining({ deliveryFee: 0 }),
    );
  });
  it("rounds percentage discounts down and caps absolute discounts", () => {
    expect(discountFor(999, { couponStrategy: "PERCENTAGE", value: 10 })).toBe(
      99,
    );
    expect(discountFor(100, { couponStrategy: "ABSOLUTE", value: 1000 })).toBe(
      100,
    );
  });
});
