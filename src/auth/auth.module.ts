import { KakaoService, KakaoLoginDto, KakaoRegisterDto } from "./kakao.service";
import {
    Body,
    CanActivate,
    Controller,
    ExecutionContext,
    Global,
    HttpCode,
    Injectable,
    Module,
    Post,
    SetMetadata,
    UnauthorizedException,
} from "@nestjs/common";
import { ConfigService } from "@nestjs/config";
import { APP_GUARD, Reflector } from "@nestjs/core";
import { JwtModule, JwtService } from "@nestjs/jwt";
import { IsNotEmpty, IsString, MaxLength } from "class-validator";
import { compare } from "bcryptjs";
import { DataSource } from "typeorm";
import { Member, NormalMember } from "../database/entities";
import { type UserRequest } from "../common/http";
export const Public = () => SetMetadata("public", true);
export class LoginDto {
    @IsString() @IsNotEmpty() @MaxLength(20) loginId: string;
    @IsString() @IsNotEmpty() @MaxLength(100) password: string;
}
@Injectable()
export class AuthService {
    constructor(
        private readonly db: DataSource,
        private readonly jwt: JwtService,
    ) {}
    async login(dto: LoginDto) {
        const account = await this.db.getRepository(NormalMember).findOneBy({ loginId: dto.loginId });
        const member =
            account && (await this.db.getRepository(Member).findOneBy({ id: account.memberId, isDeleted: false }));
   
        if (
            !account ||
            !member ||
            !(await compare(
                dto.password,
                account.password.replace(/^\$2y\$/, "$2b$"),
            ))
        )
            throw new UnauthorizedException("로그인 정보가 올바르지 않습니다.");
        const { name, nickname, imageUrl, phoneNumber } = member;
        return {
            name,
            nickname,
            imageUrl,
            phoneNumber,
            token: await this.jwt.signAsync({}, { subject: String(member.id) }),
        };
    }
}
@Injectable()
export class AuthGuard implements CanActivate {
    constructor(
        private readonly reflector: Reflector,
        private readonly jwt: JwtService,
        private readonly db: DataSource,
    ) {}
    async canActivate(context: ExecutionContext) {
        const request = context.switchToHttp().getRequest<UserRequest>();
        const isPublic = this.reflector.getAllAndOverride<boolean>("public", [
            context.getHandler(),
            context.getClass(),
        ]);
        const header = request.headers.authorization;
        if (!header && isPublic) return true;
        if (!header?.startsWith("Bearer ")) throw new UnauthorizedException();
        try {
            const payload = await this.jwt.verifyAsync<{ sub: string }>(header.slice(7));
            const id = Number(payload.sub);
            if (
                !Number.isSafeInteger(id) ||
                id < 1 ||
                !(await this.db.getRepository(Member).existsBy({ id, isDeleted: false }))
            )
                throw new Error();
            request.memberId = id;
            return true;
        } catch {
            throw new UnauthorizedException("유효하지 않은 인증 정보입니다.");
        }
    }
}
@Controller("auth")
class AuthController {
    constructor(private readonly auth: AuthService, private readonly kakao: KakaoService) {}
    @Public() @Post("kakao/login") @HttpCode(200) kakaoLogin(@Body() dto: KakaoLoginDto) { return this.kakao.login(dto); }
    @Public() @Post("kakao/register") @HttpCode(200) kakaoRegister(@Body() dto: KakaoRegisterDto) { return this.kakao.register(dto); }
    @Public() @Post("login") @HttpCode(200) login(@Body() dto: LoginDto) {
        return this.auth.login(dto);
    }
}
@Global()
@Module({
    imports: [
        JwtModule.registerAsync({
            inject: [ConfigService],
            useFactory: (c: ConfigService) => {
                const secret = c.getOrThrow<string>("JWT_SECRET_KEY");
                if (Buffer.byteLength(secret) < 32) throw new Error("JWT_SECRET_KEY must contain at least 32 bytes");
                return {
                    secret,
                    signOptions: {
                        expiresIn: Number(c.get("JWT_EXPIRES_IN_SECONDS", 86400)),
                    },
                    verifyOptions: { algorithms: ["HS256", "HS384", "HS512"] },
                };
            },
        }),
    ],
    controllers: [AuthController],
    providers: [KakaoService, AuthService, { provide: APP_GUARD, useClass: AuthGuard }],
    exports: [AuthService, JwtModule],
})
export class AuthModule {}
