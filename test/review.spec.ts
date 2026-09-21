import { DataSource } from "typeorm";
import { ReviewService } from "../src/review/review.module";
import { StorageService } from "../src/storage/storage.module";
describe("Review purchase authorization", () => {
  function setup(order: unknown, purchased = true, duplicate = false) {
    const em = {
      findOne: jest.fn().mockResolvedValue(order),
      existsBy: jest
        .fn()
        .mockResolvedValueOnce(purchased)
        .mockResolvedValueOnce(duplicate),
      save: jest.fn(),
    };
    const storage = {
      upload: jest
        .fn()
        .mockResolvedValue([
          { key: "new-image", url: "https://example.com/image" },
        ]),
      cleanup: jest.fn().mockResolvedValue(undefined),
    };
    const service = new ReviewService(
      {
        transaction: async (callback: (em: unknown) => unknown) => callback(em),
      } as unknown as DataSource,
      storage as unknown as StorageService,
    );
    return { service, em, storage };
  }
  const dto = {
    orderId: 1,
    itemId: 2,
    title: "리뷰",
    content: "내용",
    rating: 5,
  };
  it("rejects another member purchase and cleans up uploaded images", async () => {
    const { service, em, storage } = setup({ id: 1, memberId: 99 });
    await expect(service.create(7, dto, [])).rejects.toThrow(
      "사용자의 주문이 아닙니다.",
    );
    expect(em.save).not.toHaveBeenCalled();
    expect(storage.cleanup).toHaveBeenCalledWith([
      { key: "new-image", url: "https://example.com/image" },
    ]);
  });
  it("rejects a product that was not in the order", async () => {
    const { service } = setup({ id: 1, memberId: 7 }, false);
    await expect(service.create(7, dto, [])).rejects.toThrow(
      "주문에 포함되지 않은 제품입니다.",
    );
  });
  it("rejects duplicate reviews even when the previous review was deleted", async () => {
    const { service } = setup({ id: 1, memberId: 7 }, true, true);
    await expect(service.create(7, dto, [])).rejects.toThrow(
      "이미 작성한 리뷰가 있습니다.",
    );
  });
});
