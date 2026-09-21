import { Type } from "class-transformer";
import {
  ArrayMaxSize,
  ArrayNotEmpty,
  IsArray,
  IsBoolean,
  IsEmail,
  IsInt,
  IsOptional,
  IsString,
  Length,
  Matches,
  Max,
  MaxLength,
  Min,
} from "class-validator";
export class RegisterDto {
  @IsString() @Length(1, 45) name: string;
  @IsString() @Length(1, 10) nickname: string;
  @IsString() @Length(1, 15) phoneNumber: string;
  @IsString() @Length(5, 20) loginId: string;
  @IsString()
  @Length(8, 20)
  @Matches(
    /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*\-_=+,<>./?;:])[A-Za-z\d~!@#$%^&*\-_=+,<>./?;:]+$/,
  )
  password: string;
  @IsEmail() @MaxLength(320) email: string;
}
export class ItemIdDto {
  @Type(() => Number) @IsInt() @Min(1) itemId: number;
}
export class AmountDto {
  @IsInt() @Min(1) @Max(1000000) amount: number;
}
export class CartAddDto extends AmountDto {
  @IsInt() @Min(1) itemId: number;
}
export class CartDeleteDto {
  @IsArray()
  @ArrayNotEmpty()
  @ArrayMaxSize(100)
  @IsInt({ each: true })
  @Min(1, { each: true })
  itemIdList: number[];
}
export class AddressDto {
  @IsString() @Length(1, 45) addressAlias: string;
  @IsString() @Length(1, 20) recipient: string;
  @IsString() @Length(1, 100) address: string;
  @IsString() @MaxLength(100) detailAddress: string;
  @IsString() @Length(1, 15) phoneNumber: string;
  @IsOptional() @IsBoolean() isDefault?: boolean;
  // Jackson exposes primitive isDefault as "default"; accept both input spellings.
  @IsOptional() @IsBoolean() default?: boolean;
}
export class MailDto {
  @IsEmail() @MaxLength(320) email: string;
}
export class MailCheckDto extends MailDto {
  @IsString() @Matches(/^\d{6}$/) verificationCode: string;
}
export class FindMailDto extends MailDto {
  @IsString() @Length(1, 45) name: string;
}
export class FindIdDto extends MailCheckDto {
  @IsString() @Length(1, 45) name: string;
  @IsString() @Length(1, 100) sessionId: string;
}
