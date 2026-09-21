import { Module } from "@nestjs/common";
import { ConfigModule } from "@nestjs/config";
import { AuthModule } from "./auth/auth.module";
import { DatabaseModule } from "./database/database.module";
import { InquiryModule } from "./inquiry/inquiry.module";
import { ItemModule } from "./item/item.module";
import { MemberModule } from "./member/member.module";
import { OrderModule } from "./order/order.module";
import { ReviewModule } from "./review/review.module";
import { StorageModule } from "./storage/storage.module";
@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    DatabaseModule,
    AuthModule,
    StorageModule,
    ItemModule,
    MemberModule,
    OrderModule,
    ReviewModule,
    InquiryModule,
  ],
})
export class AppModule {}
