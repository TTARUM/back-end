import {
  Column,
  CreateDateColumn,
  Entity,
  PrimaryColumn,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from "typeorm";

abstract class Identified {
  @PrimaryGeneratedColumn() id: number;
}
abstract class Created extends Identified {
  @CreateDateColumn({ name: "created_at" }) createdAt: Date;
}
abstract class Updated extends Created {
  @UpdateDateColumn({ name: "updated_at" }) updatedAt: Date;
}

@Entity("member")
export class Member extends Updated {
  @Column({ length: 45 }) name: string;
  @Column({ length: 10, unique: true }) nickname: string;
  @Column({ name: "phone_number", length: 15 }) phoneNumber: string;
  @Column({ name: "image_url", length: 100, nullable: true, type: "varchar" })
  imageUrl: string | null;
  @Column({ name: "is_deleted", default: false }) isDeleted: boolean;
}
@Entity("normal_member")
export class NormalMember {
  @PrimaryColumn({ name: "member_id" }) memberId: number;
  @Column({ name: "login_id", length: 20, unique: true }) loginId: string;
  @Column({ length: 100 }) password: string;
  @Column({ length: 320, unique: true }) email: string;
}
@Entity("member_provider")
export class MemberProvider extends Identified {
  @Column({ length: 100 }) name: string;
}
@Entity("oauth_member")
export class OauthMember {
  @PrimaryColumn({ name: "member_id" }) memberId: number;
  @Column({ length: 320 }) email: string;
  @Column({ name: "provider_id" }) providerId: number;
}
@Entity("category")
export class Category extends Identified {
  @Column({ length: 45 }) name: string;
}
@Entity("item")
export class Item extends Created {
  @Column({ length: 100 }) name: string;
  @Column({ length: 300 }) description: string;
  @Column() price: number;
  @Column({ name: "item_image_url", length: 200 }) itemImageUrl: string;
  @Column({
    name: "item_description_image_url",
    length: 200,
    nullable: true,
    type: "varchar",
  })
  itemDescriptionImageUrl: string | null;
  @Column({ name: "category_id" }) categoryId: number;
  @Column({ name: "rating_sum", default: 0 }) ratingSum: number;
  @Column({ name: "rating_count", default: 0 }) ratingCount: number;
  @Column({ name: "order_count", default: 0 }) orderCount: number;
}
@Entity("wishlist")
export class Wishlist {
  @PrimaryColumn({ name: "member_id" }) memberId: number;
  @PrimaryColumn({ name: "item_id" }) itemId: number;
  @CreateDateColumn({ name: "created_at" }) createdAt: Date;
}
@Entity("cart")
export class Cart {
  @PrimaryColumn({ name: "member_id" }) memberId: number;
  @PrimaryColumn({ name: "item_id" }) itemId: number;
  @Column() amount: number;
}
@Entity("address")
export class Address extends Identified {
  @Column({ name: "member_id" }) memberId: number;
  @Column({ name: "address_alias", length: 45 }) addressAlias: string;
  @Column({ length: 20 }) recipient: string;
  @Column({ length: 100 }) address: string;
  @Column({ name: "detail_address", length: 100 }) detailAddress: string;
  @Column({ name: "phone_number", length: 15 }) phoneNumber: string;
  @Column({ name: "is_default", default: false }) isDefault: boolean;
}
@Entity("coupon")
export class Coupon extends Identified {
  @Column() name: string;
  @Column({ name: "coupon_strategy" }) couponStrategy: string;
  @Column() value: number;
}
@Entity("member_coupon")
export class MemberCoupon extends Created {
  @Column({ name: "member_id" }) memberId: number;
  @Column({ name: "coupon_id" }) couponId: number;
}
@Entity("order")
export class Order extends Created {
  @Column({ name: "member_id" }) memberId: number;
  @Column({ length: 20 }) status: string;
  @Column({ length: 100 }) comment: string;
  @Column({ name: "phone_number", length: 15 }) phoneNumber: string;
  @Column({ length: 100 }) address: string;
  @Column({ name: "delivery_fee" }) deliveryFee: number;
  @Column({ length: 20 }) recipient: string;
  @Column() price: number;
  @Column({ name: "discount_price" }) discountPrice: number;
  @Column({ name: "payment_method" }) paymentMethod: string;
}
@Entity("order_item")
export class OrderItem {
  @PrimaryColumn({ name: "order_id" }) orderId: number;
  @PrimaryColumn({ name: "item_id" }) itemId: number;
  @Column() amount: number;
}
@Entity("review")
export class Review extends Updated {
  @Column({ name: "member_id" }) memberId: number;
  @Column({ name: "order_id" }) orderId: number;
  @Column({ name: "item_id" }) itemId: number;
  @Column() title: string;
  @Column({ length: 500 }) content: string;
  @Column() star: number;
  @Column({ name: "is_deleted", default: false }) isDeleted: boolean;
}
@Entity("review_image")
export class ReviewImage extends Identified {
  @Column({ name: "review_id" }) reviewId: number;
  @Column({ name: "file_url", length: 100 }) fileUrl: string;
  @Column() order: number;
}
@Entity("inquiry")
export class Inquiry extends Created {
  @Column({ name: "member_id" }) memberId: number;
  @Column({ name: "item_id" }) itemId: number;
  @Column({ length: 100 }) title: string;
  @Column({ length: 1000 }) content: string;
  @Column({ name: "exist_answer", default: false }) existAnswer: boolean;
  @Column({ name: "is_secret", default: false }) isSecret: boolean;
}
@Entity("inquiry_image")
export class InquiryImage extends Identified {
  @Column({ name: "inquiry_id" }) inquiryId: number;
  @Column({ name: "file_url", length: 100 }) fileUrl: string;
}
@Entity("inquiry_answer")
export class InquiryAnswer extends Created {
  @Column({ name: "inquiry_id" }) inquiryId: number;
  @Column({ length: 1000 }) content: string;
}
export const entities = [
  Member,
  NormalMember,
  MemberProvider,
  OauthMember,
  Category,
  Item,
  Wishlist,
  Cart,
  Address,
  Coupon,
  MemberCoupon,
  Order,
  OrderItem,
  Review,
  ReviewImage,
  Inquiry,
  InquiryImage,
  InquiryAnswer,
];
