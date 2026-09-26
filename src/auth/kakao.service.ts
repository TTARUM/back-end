import {
  BadRequestException,
  ConflictException,
  Injectable,
  ServiceUnavailableException,
  UnauthorizedException,
} from "@nestjs/common";
import { ConfigService } from "@nestjs/config";
import { JwtService } from "@nestjs/jwt";
import {
  IsNotEmpty,
  IsString,
  Length,
  Matches,
  MaxLength,
} from "class-validator";
import { DataSource, EntityManager, IsNull } from "typeorm";
import {
  Coupon,
  Member,
  MemberCoupon,
  MemberProvider,
  NormalMember,
  OauthMember,
} from "../database/entities";

export class KakaoLoginDto {
  @IsString() @IsNotEmpty() @MaxLength(4096) accessToken: string;
}
export class KakaoRegisterDto extends KakaoLoginDto {
  @IsString() @Length(1, 45) @Matches(/\S/) name: string;
  @IsString() @Matches(/^01[016789]\d{7,8}$/) phoneNumber: string;
}
type KakaoProfile = { subject: string; email: string; nickname: string };
@Injectable()
export class KakaoService {
  constructor(
    private readonly db: DataSource,
    private readonly jwt: JwtService,
    private readonly config: ConfigService,
  ) {}

  private async kakao(path: string, accessToken: string): Promise<any> {
    let response: Response;
    try {
      response = await fetch("https://kapi.kakao.com" + path, {
        headers: { Authorization: "Bearer " + accessToken },
        signal: AbortSignal.timeout(10000),
      });
    } catch {
      throw new ServiceUnavailableException(
        "카카오 인증 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.",
      );
    }
    if (response.status === 401)
      throw new UnauthorizedException(
        "카카오 로그인이 만료되었습니다. 다시 로그인해주세요.",
      );
    if (!response.ok)
      throw new ServiceUnavailableException(
        "카카오 사용자 정보를 확인할 수 없습니다.",
      );
    return response.json();
  }

  async profile(accessToken: string): Promise<KakaoProfile> {
    const appId = this.config.get<string>("KAKAO_APP_ID");
    if (!appId)
      throw new ServiceUnavailableException(
        "카카오 앱 ID가 설정되지 않았습니다.",
      );
    const info = await this.kakao("/v1/user/access_token_info", accessToken);
    if (String(info.app_id) !== String(appId) || !(info.expires_in > 0))
      throw new UnauthorizedException(
        "이 서비스에서 발급한 카카오 인증 정보가 아닙니다.",
      );
    const user = await this.kakao("/v2/user/me", accessToken);
    if (!user.id || String(user.id) !== String(info.id))
      throw new UnauthorizedException();
    const account = user.kakao_account;
    if (
      !account?.email ||
      account.is_email_verified !== true ||
      account.is_email_valid !== true
    )
      throw new BadRequestException(
        "카카오의 인증된 이메일 제공 동의가 필요합니다.",
      );
    // Use the verified Kakao nickname, never a client-supplied nickname.
    return {
      subject: String(user.id),
      email: account.email,
      nickname: account.profile?.nickname?.trim() || "",
    };
  }

  private async account(em: EntityManager, profile: KakaoProfile) {
    const provider = await em.findOneBy(MemberProvider, { name: "KAKAO" });
    if (!provider)
      throw new ServiceUnavailableException(
        "카카오 DB 마이그레이션을 실행해주세요.",
      );
    let account = await em.findOneBy(OauthMember, {
      providerId: provider.id,
      providerSubject: profile.subject,
    });
    if (!account) {
      // Link pre-migration records only after Kakao verified the email.
      account = await em.findOneBy(OauthMember, {
        providerId: provider.id,
        email: profile.email,
        providerSubject: IsNull(),
      });
      if (account) {
        account.providerSubject = profile.subject;
        await em.save(OauthMember, account);
      }
    }
    if (!account) return { provider, member: null };
    const member = await em.findOneBy(Member, {
      id: account.memberId,
      isDeleted: false,
    });
    if (!member)
      throw new UnauthorizedException("탈퇴한 계정은 로그인할 수 없습니다.");
    return { provider, member };
  }

  private async loginResult(member: Member) {
    const { name, nickname, imageUrl, phoneNumber } = member;
    return {
      needsRegistration: false,
      user: {
        name,
        nickname,
        imageUrl,
        phoneNumber,
        token: await this.jwt.signAsync({}, { subject: String(member.id) }),
      },
    };
  }

  async login(dto: KakaoLoginDto) {
    const profile = await this.profile(dto.accessToken);
    const { member } = await this.db.transaction((em) =>
      this.account(em, profile),
    );
    if (member) return this.loginResult(member);
    if (!profile.nickname || profile.nickname.length > 10)
      throw new BadRequestException(
        "카카오 닉네임은 1~10자여야 합니다. 카카오 프로필을 확인해주세요.",
      );
    return {
      needsRegistration: true,
      profile: { nickname: profile.nickname, email: profile.email },
    };
  }

  async register(dto: KakaoRegisterDto) {
    const profile = await this.profile(dto.accessToken);
    if (!profile.nickname || profile.nickname.length > 10)
      throw new BadRequestException(
        "카카오 닉네임은 1~10자여야 합니다. 카카오 프로필을 확인해주세요.",
      );
    try {
      const member = await this.db.transaction(async (em) => {
        const existing = await this.account(em, profile);
        if (existing.member) return existing.member;
        if (
          (await em.existsBy(NormalMember, { email: profile.email })) ||
          (await em.existsBy(OauthMember, { email: profile.email }))
        )
          throw new ConflictException(
            "이미 가입된 이메일입니다. 기존 로그인 방법을 이용해주세요.",
          );
        const nickname = profile.nickname;
        if (!nickname || (await em.existsBy(Member, { nickname })))
          throw new ConflictException(
            "이미 사용 중인 카카오 닉네임입니다. 카카오 프로필 닉네임을 변경한 뒤 다시 로그인해주세요.",
          );
        const coupon = await em.findOneBy(Coupon, { id: 1 });
        if (!coupon)
          throw new ServiceUnavailableException(
            "신규 가입 쿠폰이 설정되지 않았습니다.",
          );
        const created = await em.save(
          Member,
          em.create(Member, {
            name: dto.name.trim(),
            nickname,
            phoneNumber: dto.phoneNumber,
          }),
        );
        await em.insert(OauthMember, {
          memberId: created.id,
          providerId: existing.provider.id,
          providerSubject: profile.subject,
          email: profile.email,
        });
        await em.insert(MemberCoupon, {
          memberId: created.id,
          couponId: coupon.id,
        });
        return created;
      });
      return this.loginResult(member);
    } catch (error) {
      if ((error as { code?: string }).code === "ER_DUP_ENTRY")
        throw new ConflictException(
          "이미 등록된 회원 정보입니다. 다시 로그인하거나 닉네임을 확인해주세요.",
        );
      throw error;
    }
  }
}
