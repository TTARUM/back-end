import {
  BadRequestException,
  ForbiddenException,
  Injectable,
} from "@nestjs/common";
import { hash } from "bcryptjs";
import { DataSource, In } from "typeorm";
import {
  Address,
  Cart,
  Category,
  Coupon,
  Item,
  Member,
  MemberCoupon,
  NormalMember,
  OauthMember,
  Wishlist,
} from "../database/entities";
import { PageDto, pagination } from "../common/http";
import { ItemService } from "../item/item.module";
import { StorageService } from "../storage/storage.module";
import { AddressDto, CartAddDto, RegisterDto } from "./member.dto";
@Injectable()
export class MemberService {
  constructor(
    private readonly db: DataSource,
    private readonly items: ItemService,
    private readonly storage: StorageService,
  ) {}
  async register(dto: RegisterDto) {
    const password = await hash(dto.password, 10);
    return this.db.transaction(async (em) => {
      if (
        (await em.existsBy(Member, { nickname: dto.nickname })) ||
        (await em.existsBy(NormalMember, [
          { loginId: dto.loginId },
          { email: dto.email },
        ])) ||
        (await em.existsBy(OauthMember, { email: dto.email }))
      )
        throw new BadRequestException("이미 가입된 정보입니다.");
      const member = await em.save(
        Member,
        em.create(Member, {
          name: dto.name,
          nickname: dto.nickname,
          phoneNumber: dto.phoneNumber,
        }),
      );
      await em.insert(NormalMember, {
        memberId: member.id,
        loginId: dto.loginId,
        password,
        email: dto.email,
      });
      const coupon = await em.findOneBy(Coupon, { id: 1 });
      if (!coupon)
        throw new BadRequestException("신규 가입 쿠폰이 설정되지 않았습니다.");
      await em.insert(MemberCoupon, {
        memberId: member.id,
        couponId: coupon.id,
      });
      return { memberId: member.id };
    });
  }
  async withdraw(id: number) {
    await this.db.getRepository(Member).update(id, { isDeleted: true });
  }
  async profile(id: number, file: Express.Multer.File) {
    if (!file) throw new BadRequestException("이미지가 필요합니다.");
    const files = await this.storage.upload([file]);
    try {
      await this.db
        .getRepository(Member)
        .update(id, { imageUrl: files[0].url });
      return files[0].url;
    } catch (e) {
      await this.storage.cleanup(files);
      throw e;
    }
  }
  async wish(memberId: number, itemId: number) {
    await this.items.get(itemId);
    if (await this.db.getRepository(Wishlist).existsBy({ memberId, itemId }))
      throw new BadRequestException("이미 찜한 제품입니다.");
    await this.db.getRepository(Wishlist).insert({ memberId, itemId });
    return {};
  }
  async unwish(memberId: number, itemId: number) {
    await this.db.getRepository(Wishlist).delete({ memberId, itemId });
    return {};
  }
  async wishes(memberId: number, q: PageDto) {
    const rows = await this.db
      .getRepository(Wishlist)
      .find({
        where: { memberId },
        ...pagination(q, 8),
        order: { itemId: "ASC" },
      });
    return {
      wishlist: await Promise.all(
        rows.map(async (w) => {
          const i = await this.items.summary(await this.items.get(w.itemId));
          return {
            itemId: i.id,
            name: i.name,
            categoryName: i.categoryName,
            price: i.price,
            rating: i.rating,
            imageUrl: i.imageUrl,
            createdAt: w.createdAt,
          };
        }),
      ),
    };
  }
  async addCart(memberId: number, dto: CartAddDto) {
    await this.items.get(dto.itemId);
    await this.db.transaction(async (em) => {
      await em.findOneOrFail(Member, {
        where: { id: memberId },
        lock: { mode: "pessimistic_write" },
      });
      const cart = await em.findOneBy(Cart, { memberId, itemId: dto.itemId });
      const amount = (cart?.amount ?? 0) + dto.amount;
      if (amount > 1000000)
        throw new BadRequestException("수량 한도를 초과했습니다.");
      await em.save(Cart, { memberId, itemId: dto.itemId, amount });
    });
    return {};
  }
  async carts(memberId: number) {
    const carts = await this.db.getRepository(Cart).findBy({ memberId });
    return Promise.all(
      carts.map(async (c) => {
        const i = await this.items.get(c.itemId);
        const category = await this.db
          .getRepository(Category)
          .findOneBy({ id: i.categoryId });
        return {
          itemId: i.id,
          itemName: i.name,
          categoryName: category?.name,
          itemImageUrl: i.itemImageUrl,
          price: i.price,
          amount: c.amount,
        };
      }),
    );
  }
  async updateCart(memberId: number, itemId: number, amount: number) {
    const result = await this.db
      .getRepository(Cart)
      .update({ memberId, itemId }, { amount });
    if (!result.affected)
      throw new BadRequestException("장바구니 제품이 없습니다.");
    return {};
  }
  async deleteCart(memberId: number, ids: number[]) {
    await this.db.getRepository(Cart).delete({ memberId, itemId: In(ids) });
    return {};
  }
  async addresses(memberId: number) {
    return (await this.db.getRepository(Address).findBy({ memberId })).map(
      (a) => ({
        addressId: a.id,
        addressAlias: a.addressAlias,
        recipient: a.recipient,
        address: a.address,
        detailAddress: a.detailAddress,
        phoneNumber: a.phoneNumber,
        default: a.isDefault,
      }),
    );
  }
  async address(memberId: number, dto: AddressDto, id?: number) {
    await this.db.transaction(async (em) => {
      await em.findOneOrFail(Member, {
        where: { id: memberId },
        lock: { mode: "pessimistic_write" },
      });
      const old = id ? await em.findOneBy(Address, { id }) : null;
      if (id && !old) throw new BadRequestException("배송지가 없습니다.");
      if (old && old.memberId !== memberId) throw new ForbiddenException();
      const isDefault = dto.isDefault ?? dto.default ?? false;
      if (isDefault)
        await em.update(Address, { memberId }, { isDefault: false });
      const { default: ignored, isDefault: ignoredDefault, ...fields } = dto;
      await em.save(Address, {
        ...fields,
        ...(id ? { id } : {}),
        memberId,
        isDefault,
      });
    });
    return {};
  }
  async deleteAddress(memberId: number, id: number) {
    await this.db.transaction(async (em) => {
      await em.findOneOrFail(Member, {
        where: { id: memberId },
        lock: { mode: "pessimistic_write" },
      });
      const address = await em.findOneBy(Address, { id });
      if (!address) throw new BadRequestException("배송지가 없습니다.");
      if (address.memberId !== memberId) throw new ForbiddenException();
      if (address.isDefault)
        throw new BadRequestException("기본 배송지는 삭제할 수 없습니다.");
      await em.delete(Address, id);
    });
    return {};
  }
  async coupons(memberId: number) {
    const owned = await this.db
      .getRepository(MemberCoupon)
      .findBy({ memberId });
    return this.db
      .getRepository(Coupon)
      .findBy({ id: In(owned.map((c) => c.couponId)) });
  }
}
