import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  Module,
  Param,
  ParseIntPipe,
  Post,
  Put,
  Query,
  Req,
  UploadedFile,
  UseInterceptors,
} from "@nestjs/common";
import { FileInterceptor } from "@nestjs/platform-express";
import { Public } from "../auth/auth.module";
import { PageDto, type UserRequest } from "../common/http";
import { ItemModule } from "../item/item.module";
import { uploadOptions } from "../storage/storage.module";
import {
  AddressDto,
  AmountDto,
  CartAddDto,
  CartDeleteDto,
  FindIdDto,
  FindMailDto,
  ItemIdDto,
  MailCheckDto,
  MailDto,
  RegisterDto,
} from "./member.dto";
import { MemberService } from "./member.service";
import { MailService } from "./mail.service";
@Controller("members")
export class MemberController {
  constructor(
    private readonly members: MemberService,
    private readonly mail: MailService,
  ) {}
  @Public() @Post("register") @HttpCode(200) register(@Body() d: RegisterDto) {
    return this.members.register(d);
  }
  @Delete("withdraw") withdraw(@Req() r: UserRequest) {
    return this.members.withdraw(r.memberId!);
  }
  @Post("profile-image")
  @HttpCode(200)
  @UseInterceptors(FileInterceptor("image", uploadOptions))
  profile(@Req() r: UserRequest, @UploadedFile() f: Express.Multer.File) {
    return this.members.profile(r.memberId!, f);
  }
  @Post("wish-item") @HttpCode(200) wish(
    @Req() r: UserRequest,
    @Body() d: ItemIdDto,
  ) {
    return this.members.wish(r.memberId!, d.itemId);
  }
  @Get("wish-item") wishes(@Req() r: UserRequest, @Query() q: PageDto) {
    return this.members.wishes(r.memberId!, q);
  }
  @Delete("wish-item") unwish(@Req() r: UserRequest, @Query() q: ItemIdDto) {
    return this.members.unwish(r.memberId!, q.itemId);
  }
  @Post("carts") @HttpCode(200) addCart(
    @Req() r: UserRequest,
    @Body() d: CartAddDto,
  ) {
    return this.members.addCart(r.memberId!, d);
  }
  @Get("carts") carts(@Req() r: UserRequest) {
    return this.members.carts(r.memberId!);
  }
  @Put("carts/:id") updateCart(
    @Req() r: UserRequest,
    @Param("id", ParseIntPipe) id: number,
    @Body() d: AmountDto,
  ) {
    return this.members.updateCart(r.memberId!, id, d.amount);
  }
  @Delete("carts") deleteCart(@Req() r: UserRequest, @Body() d: CartDeleteDto) {
    return this.members.deleteCart(r.memberId!, d.itemIdList);
  }
  @Get("address") addresses(@Req() r: UserRequest) {
    return this.members.addresses(r.memberId!);
  }
  @Post("address") @HttpCode(200) address(
    @Req() r: UserRequest,
    @Body() d: AddressDto,
  ) {
    return this.members.address(r.memberId!, d);
  }
  @Post("address/:id") @HttpCode(200) updateAddress(
    @Req() r: UserRequest,
    @Param("id", ParseIntPipe) id: number,
    @Body() d: AddressDto,
  ) {
    return this.members.address(r.memberId!, d, id);
  }
  @Delete("address/:id") deleteAddress(
    @Req() r: UserRequest,
    @Param("id", ParseIntPipe) id: number,
  ) {
    return this.members.deleteAddress(r.memberId!, id);
  }
  @Get("coupons") coupons(@Req() r: UserRequest) {
    return this.members.coupons(r.memberId!);
  }
  @Public() @Post("mail/send") @HttpCode(200) send(@Body() d: MailDto) {
    return this.mail.send(d.email);
  }
  @Public() @Post("mail/check") @HttpCode(200) check(@Body() d: MailCheckDto) {
    return this.mail.check(d.email, d.verificationCode, "register");
  }
  @Public() @Post("mail/send/find-id") @HttpCode(200) sendFind(
    @Body() d: FindMailDto,
  ) {
    return this.mail.send(d.email, d.name);
  }
  @Public() @Post("mail/check/find-id") @HttpCode(200) checkFind(
    @Body() d: MailCheckDto,
  ) {
    return this.mail.check(d.email, d.verificationCode, "find");
  }
  @Public() @Get("mail/find-id") find(@Query() d: FindIdDto) {
    return this.mail.find(d);
  }
}
@Module({
  imports: [ItemModule],
  controllers: [MemberController],
  providers: [MemberService, MailService],
})
export class MemberModule {}
