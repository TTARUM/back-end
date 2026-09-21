import {
  ArgumentsHost,
  BadRequestException,
  Catch,
  ExceptionFilter,
  HttpException,
  Logger,
  ValidationPipe,
} from "@nestjs/common";
import { Type, plainToInstance } from "class-transformer";
import { IsInt, IsOptional, Max, Min, validateSync } from "class-validator";
import { Request, Response } from "express";
export interface UserRequest extends Request {
  memberId?: number;
}
export class PageDto {
  @IsOptional() @Type(() => Number) @IsInt() @Min(0) @Max(1000000) page = 0;
  @IsOptional() @Type(() => Number) @IsInt() @Min(1) @Max(100) size?: number;
}
export function pagination(query: PageDto, defaultSize = 10) {
  const take = query.size ?? defaultSize;
  return { skip: query.page * take, take };
}
export const validationPipe = () =>
  new ValidationPipe({
    transform: true,
    whitelist: true,
    forbidNonWhitelisted: true,
  });
export function multipartDto<T extends object>(
  type: new () => T,
  input: unknown,
): T {
  let value: unknown;
  try {
    value = typeof input === "string" ? JSON.parse(input) : input;
  } catch {
    throw new BadRequestException("올바른 JSON 요청이 필요합니다.");
  }
  if (!value || typeof value !== "object" || Array.isArray(value))
    throw new BadRequestException("요청 데이터가 필요합니다.");
  const dto = plainToInstance(type, value);
  const errors = validateSync(dto, {
    whitelist: true,
    forbidNonWhitelisted: true,
  });
  if (errors.length)
    throw new BadRequestException(
      errors.flatMap((e) => Object.values(e.constraints ?? {})).join(", "),
    );
  return dto;
}
@Catch()
export class ApiExceptionFilter implements ExceptionFilter {
  private readonly logger = new Logger(ApiExceptionFilter.name);
  catch(error: unknown, host: ArgumentsHost) {
    const response = host.switchToHttp().getResponse<Response>();
    const status = error instanceof HttpException ? error.getStatus() : 500;
    if (status === 500) this.logger.error(error);
    const detail =
      error instanceof HttpException
        ? error.getResponse()
        : "서버 오류가 발생했습니다.";
    const message =
      typeof detail === "string"
        ? detail
        : (detail as { message: unknown }).message;
    response
      .status(status)
      .json({
        dateTime: new Date().toISOString(),
        message: Array.isArray(message) ? message.join(", ") : message,
      });
  }
}
