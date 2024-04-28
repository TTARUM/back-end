package com.ttarum.member.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.member.dto.request.*;
import com.ttarum.member.dto.response.AddressResponse;
import com.ttarum.member.dto.response.CartResponse;
import com.ttarum.member.dto.response.WishlistResponse;
import com.ttarum.member.mail.dto.EmailCheckDTO;
import com.ttarum.member.mail.dto.MailRequest;
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

import java.util.List;
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
    public ResponseEntity<Void> registerNormalMember(@RequestBody NormalMemberRegister dto) {
        memberService.registerNormalUser(dto.toMemberEntity(), dto.toNormalMemberEntity());
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> withdrawMember(@AuthenticationPrincipal final CustomUserDetails user) {
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
    public ResponseEntity<Void> updateProfileImage(
            @Parameter(description = "프로필 이미지 파일", required = true) @RequestPart("image") final MultipartFile image,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.updateProfileImage(user.getId(), image);
        return ResponseEntity.ok().build();
    }

    /**
     * 제품 찜 메서드
     *
     * @param user   로그인한 회원
     * @param itemId 찜 목록에 추가할 제품의 Id 값
     * @return 빈 응답
     */
    @Operation(summary = "제품 찜하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @Parameter(name = "itemId", required = true, description = "제품의 Id 값", example = "1")
    @PostMapping("/wish-item")
    public ResponseEntity<Void> addItemToWishlist(
            @RequestParam final long itemId,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.wishItem(user.getId(), itemId);
        return ResponseEntity.ok().build();
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
            @Parameter(name = "page", description = "페이지 넘버 (기본 값 0)", example = "1"),
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
    public ResponseEntity<Void> addToCart(
            @RequestBody final CartAdditionRequest cartAdditionRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.addToCart(user.getId(), cartAdditionRequest);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> addAddress(
            @RequestBody final AddressUpsertRequest addressUpsertRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.addAddress(user.getId(), addressUpsertRequest);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> updateAddress(
            @PathVariable final Long addressId,
            @RequestBody final AddressUpsertRequest addressUpsertRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.updateAddress(user.getId(), addressId, addressUpsertRequest);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> deleteAddress(
            @PathVariable final Long addressId,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.deleteAddress(user.getId(), addressId);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> deleteFromCart(
            @RequestBody final CartDeletionRequest cartDeletionRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.deleteFromCart(user.getId(), cartDeletionRequest);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> updateItemAmountInCart(
            @PathVariable final long itemId,
            @RequestBody final CartUpdateRequest cartUpdateRequest,
            @AuthenticationPrincipal final CustomUserDetails user
    ) {
        memberService.updateItemAmountInCart(user.getId(), itemId, cartUpdateRequest);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "회원가입 과정의 이메일 인증 코드 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping("/mail/send")
    public ResponseEntity<Void> sendVerificationCodeToRegister(@RequestBody @Valid final MailRequest mailRequest) {
        emailService.sendVerificationCodeToRegister(mailRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원가입 과정의 이메일 인증 코드 확인")
    @ApiResponse(responseCode = "200", description = "성공")
    @PostMapping("/mail/check")
    public ResponseEntity<Void> checkVerificationCodeToRegister(@RequestBody @Valid final EmailCheckDTO emailCheckDTO) {
        boolean checked = emailService.checkVerificationCode(emailCheckDTO.getEmail(), emailCheckDTO.getVerificationCode());
        if (checked) {
            return ResponseEntity.ok().build();
        }
        throw MailException.getInstance(VALIDATING);
    }
}
