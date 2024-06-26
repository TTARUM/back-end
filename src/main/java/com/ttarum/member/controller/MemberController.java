package com.ttarum.member.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.member.dto.request.*;
import com.ttarum.member.dto.response.AddressResponse;
import com.ttarum.member.dto.response.CartResponse;
import com.ttarum.member.dto.response.CouponResponse;
import com.ttarum.member.dto.response.WishlistResponse;
import com.ttarum.member.mail.EmailStatus;
import com.ttarum.member.mail.EmailVerification;
import com.ttarum.member.mail.dto.EmailCheckDTO;
import com.ttarum.member.mail.dto.MailRequest;
import com.ttarum.member.mail.dto.request.FindingEmailRequest;
import com.ttarum.member.mail.dto.request.MailRequestToFindId;
import com.ttarum.member.mail.dto.response.CheckVerificationCodeResponse;
import com.ttarum.member.mail.dto.response.FindingEmailResponse;
import com.ttarum.member.mail.exception.MailException;
import com.ttarum.member.service.EmailService;
import com.ttarum.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ttarum.member.mail.exception.ErrorType.VALIDATING;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "Member", description = "회원")
public class MemberController {
    private static final Integer DEFAULT_WISHLIST_SIZE = 8;

    private final MemberService memberService;
    private final EmailService emailService;

    /**
     * 회원가입 메서드
     *
     * @param dto 회원가입에 필요한 데이터가 담긴 객체
     * @return 빈 응답
     */
    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패")
    })
    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> registerNormalMember(@RequestBody NormalMemberRegister dto) {
        long memberId = memberService.registerNormalUser(dto.toMemberEntity(), dto.toNormalMemberEntity());
        HashMap<String, Object> body = new HashMap<>();
        body.put("memberId", memberId);
        return ResponseEntity.ok(body);
    }

    /**
     * 회원 탈퇴 메서드
     *
     * @param user 로그인한 회원
     * @return 빈 응답
     */
    @Operation(summary = "회원 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "탈퇴 실패")
    })
    @DeleteMapping("/withdraw")
    public ResponseEntity<Object> withdrawMember(@AuthenticationPrincipal final CustomUserDetails user) {
        memberService.withdraw(user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * 프로필 이미지 업데이트 메서드
     *
     * @param user  로그인한 회원
     * @param image 업데이트할 이미지 파일
     * @return 빈 응답
     */
    @Operation(summary = "프로필 이미지 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping(value = "/profile-image", consumes = "multipart/form-data")
    public ResponseEntity<String> updateProfileImage(
            @Parameter(description = "프로필 이미지 파일", required = true) @RequestPart("image") final MultipartFile image,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        String ret = memberService.updateProfileImage(user.getId(), image);
        return ResponseEntity.ok(ret);
    }

    /**
     * 제품 찜 메서드
     *
     * @param user            로그인한 회원
     * @param wishItemRequest 찜 목록에 추가할 제품의 Id 값
     * @return 빈 응답
     */
    @Operation(summary = "제품 찜하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @Parameter(name = "itemId", required = true, description = "제품의 Id 값", example = "1")
    @PostMapping("/wish-item")
    public ResponseEntity<Map<String, Object>> addItemToWishlist(
            @RequestBody final WishItemRequest wishItemRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.wishItem(user.getId(), wishItemRequest.getItemId());
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    /**
     * 찜 목록 조회 메서드
     *
     * @param user 로그인한 회원
     * @param page 페이지
     * @param size 페이지당 찜한 제품 개수
     * @return 찜 목록 리스트
     */
    @Operation(summary = "찜 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @Parameters(value = {
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "0"),
            @Parameter(name = "size", description = "페이지 당 찜한 제품 개수 (기본 값 8)", example = "8")
    })
    @GetMapping("/wish-item")
    public ResponseEntity<WishlistResponse> getWishlist(
            final Optional<Integer> page,
            final Optional<Integer> size,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(DEFAULT_WISHLIST_SIZE));
        WishlistResponse wishlistResponse = memberService.getWishlistResponse(user.getId(), pageRequest);
        return ResponseEntity.ok(wishlistResponse);
    }

    /**
     * 찜 목록에서 제품 제거
     *
     * @param user   로그인한 회원
     * @param itemId 제거할 제품의 Id 값
     * @return 빈 응답
     */
    @Operation(summary = "찜 목록에서 제품 제거")
    @ApiResponse(responseCode = "200", description = "제거 성공")
    @Parameter(name = "itemId", description = "제거할 제품의 Id 값", example = "1")
    @DeleteMapping("/wish-item")
    public ResponseEntity<Map<String, Object>> deleteWishList(@AuthenticationPrincipal final CustomUserDetails user, @RequestParam final Long itemId) {
        memberService.deleteItemFromWishList(user.getId(), itemId);
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    /**
     * 장바구니에 제품 추가
     *
     * @param user                로그인한 회원
     * @param cartAdditionRequest 추가될 제품과 수량이 담긴 객체
     * @return 빈 응답
     */
    @Operation(summary = "장바구니에 제품 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @Parameters(value = {
            @Parameter(name = "itemId", description = "제품의 Id 값", example = "1", required = true),
            @Parameter(name = "amount", description = "장바구니에 담을 제품의 수량", example = "1", required = true),
            @Parameter(name = "cartAdditionRequest", hidden = true)
    })
    @PostMapping("/carts")
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestBody final CartAdditionRequest cartAdditionRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.addToCart(user.getId(), cartAdditionRequest);
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    /**
     * 장바구니 조회 메서드
     *
     * @param user 로그인한 회원
     * @return 장바구니에 담긴 제품 목록
     */
    @Operation(summary = "장바구니 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/carts")
    public ResponseEntity<List<CartResponse>> getCartList(@AuthenticationPrincipal final CustomUserDetails user) {
        List<CartResponse> cartResponseList = memberService.getCartResponseList(user.getId());
        return ResponseEntity.ok(cartResponseList);
    }

    /**
     * 배송지 추가
     *
     * @param user                 로그인한 사용자
     * @param addressUpsertRequest 추가될 주소 정보
     * @return 빈 응답
     */
    @Operation(summary = "배송지 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping("/address")
    public ResponseEntity<Map<String, Object>> addAddress(
            @RequestBody final AddressUpsertRequest addressUpsertRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.addAddress(user.getId(), addressUpsertRequest);
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    /**
     * 배송지 조회(최근 사용 순으로 정렬)
     *
     * @param user 로그인한 사용자
     * @return 배송지 목록
     */
    @Operation(summary = "배송지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @GetMapping("/address")
    public ResponseEntity<List<AddressResponse>> getAddressList(@AuthenticationPrincipal final CustomUserDetails user) {
        List<AddressResponse> addressResponseList = memberService.getAddressList(user.getId())
                .stream()
                .map(AddressResponse::fromAddress)
                .toList();
        return ResponseEntity.ok(addressResponseList);
    }

    /**
     * 배송지 수정
     *
     * @param user                 로그인한 사용자
     * @param addressUpsertRequest 추가될 주소 정보
     * @return 빈 응답
     */
    @Operation(summary = "배송지 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping("/address/{addressId}")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @PathVariable final Long addressId,
            @RequestBody final AddressUpsertRequest addressUpsertRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.updateAddress(user.getId(), addressId, addressUpsertRequest);
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    /**
     * 배송지 삭제
     *
     * @param user      로그인한 사용자
     * @param addressId 삭제할 주소의 Id 값
     * @return 빈 응답
     */
    @Operation(summary = "배송지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Map<String, Object>> deleteAddress(
            @PathVariable final Long addressId,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.deleteAddress(user.getId(), addressId);
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    /**
     * 장바구니에서 제품 제거
     *
     * @param cartDeletionRequest 제거할 제품의 Id 값이 담긴 객체
     * @param user                로그인한 사용자
     * @return 빈 응답
     */
    @Operation(summary = "장바구니에서 제품 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제거 성공"),
            @ApiResponse(responseCode = "400", description = "제거 실패")
    })
    @DeleteMapping("/carts")
    public ResponseEntity<Map<String, Object>> deleteFromCart(
            @RequestBody final CartDeletionRequest cartDeletionRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.deleteFromCart(user.getId(), cartDeletionRequest);
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    /**
     * 장바구니 수량 업데이트
     *
     * @param itemId            변경될 제품의 Id 값
     * @param user              로그인한 사용자
     * @param cartUpdateRequest 수정될 제품 정보
     * @return 빈 응답
     */
    @Operation(summary = "장바구니 수량 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "업데이트 실패")
    })
    @Parameters(value = {
            @Parameter(name = "itemId", description = "제품의 Id 값", example = "1"),
            @Parameter(name = "amount", description = "수정할 제품의 양", example = "3"),
            @Parameter(name = "cartUpdateRequest", hidden = true)
    })
    @PutMapping("/carts/{itemId}")
    public ResponseEntity<Map<String, Object>> updateItemAmountInCart(
            @PathVariable final long itemId,
            @RequestBody final CartUpdateRequest cartUpdateRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.updateItemAmountInCart(user.getId(), itemId, cartUpdateRequest);
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "회원가입 과정의 이메일 인증 코드 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping("/mail/send")
    public ResponseEntity<Map<String, Object>> sendVerificationCodeToRegister(@RequestBody @Valid final MailRequest mailRequest) {
        emailService.sendVerificationCodeToRegister(mailRequest.getEmail());
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "회원가입 과정의 이메일 인증 코드 확인")
    @ApiResponse(responseCode = "200", description = "성공")
    @PostMapping("/mail/check")
    public ResponseEntity<Map<String, Object>> checkVerificationCodeToRegister(@RequestBody @Valid final EmailCheckDTO emailCheckDTO) {
        boolean checked = emailService.checkVerificationCode(emailCheckDTO.getEmail(), emailCheckDTO.getVerificationCode());
        if (checked) {
            Map<String, Object> body = new HashMap<>();
            return ResponseEntity.ok(body);
        }
        throw MailException.getInstance(VALIDATING);
    }

    @Operation(summary = "쿠폰 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @GetMapping("/coupons")
    public ResponseEntity<List<CouponResponse>> getCouponList(@AuthenticationPrincipal final CustomUserDetails user) {
        List<CouponResponse> ret = memberService.getCouponList(user.getId());
        return ResponseEntity.ok(ret);
    }

    @Operation(summary = "아이디 찾기 - 인증 번호 전송")
    @PostMapping("/mail/send/find-id")
    public ResponseEntity<Map<String, Object>> sendVerificationCodeToFindId(@RequestBody @Valid final MailRequestToFindId mailRequest) {
        emailService.sendVerificationCodeToFindId(mailRequest);
        Map<String, Object> body = new HashMap<>();
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "아이디 찾기 - 인증 번호 확인")
    @Parameters(value = {
            @Parameter(name = "email", description = "이메일", example = "adsf@adf.com"),
            @Parameter(name = "verificationCode", description = "인증 번호", example = "123456")
    })
    @PostMapping("/mail/check/find-id")
    public ResponseEntity<CheckVerificationCodeResponse> checkVerificationCodeToFindId(@RequestBody @Valid final EmailCheckDTO emailCheckDTO) {
        EmailVerification emailVerification = emailService.checkVerificationCodeToFindId(emailCheckDTO.getEmail(), emailCheckDTO.getVerificationCode());
        if (emailVerification.getStatus().equals(EmailStatus.VALID)) {
            return ResponseEntity.ok(new CheckVerificationCodeResponse(emailVerification.getUuid()));
        }
        throw MailException.getInstance(VALIDATING);
    }

    @Operation(summary = "아이디 찾기")
    @Parameters(value = {
            @Parameter(name = "name", description = "이름", example = "홍길동"),
            @Parameter(name = "email", description = "이메일", example = "asdf@adsf.com"),
            @Parameter(name = "verificationCode", description = "인증 번호", example = "123456"),
            @Parameter(name = "sessionId", description = "세션 Id", example = "asdfasaoeirj..")
    })
    @GetMapping("/mail/find-id")
    public ResponseEntity<FindingEmailResponse> findEmail(@RequestParam final FindingEmailRequest findingEmailRequest) {
        String email = emailService.findEmail(findingEmailRequest);
        return ResponseEntity.ok(new FindingEmailResponse(email));
    }
}
