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
public class MemberControllerImpl implements MemberController {


    private static final Integer DEFAULT_WISHLIST_SIZE = 8;

    private final MemberService memberService;
    private final EmailService emailService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<Void> registerNormalMember(NormalMemberRegister dto) {
        memberService.registerNormalUser(dto.toMemberEntity(), dto.toNormalMemberEntity());
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdrawMember(@AuthenticationPrincipal final CustomUserDetails user) {
        memberService.withdraw(user.getId());
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/profile-image")
    public ResponseEntity<Void> updateProfileImage(@AuthenticationPrincipal final CustomUserDetails user,
                                                   @RequestPart(name = "image") final MultipartFile image) {
        memberService.updateProfileImage(user.getId(), image);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/wish-item")
    public ResponseEntity<Void> addItemToWishlist(@AuthenticationPrincipal final CustomUserDetails user, @RequestParam final long itemId) {
        memberService.wishItem(user.getId(), itemId);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/wish-item")
    public ResponseEntity<WishlistResponse> getWishlist(@AuthenticationPrincipal final CustomUserDetails user,
                                                        final Optional<Integer> page,
                                                        final Optional<Integer> size) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(DEFAULT_WISHLIST_SIZE));
        WishlistResponse wishlistResponse = memberService.getWishlistResponse(user.getId(), pageRequest);
        return ResponseEntity.ok(wishlistResponse);
    }

    @Override
    @PostMapping("/carts")
    public ResponseEntity<Void> addToCart(@AuthenticationPrincipal final CustomUserDetails user, final CartAdditionRequest cartAdditionRequest) {
        memberService.addToCart(user.getId(), cartAdditionRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/carts")
    public ResponseEntity<List<CartResponse>> getCartList(@AuthenticationPrincipal final CustomUserDetails user) {
        List<CartResponse> cartResponseList = memberService.getCartResponseList(user.getId());
        return ResponseEntity.ok(cartResponseList);
    }

    @Override
    @PostMapping("/address")
    public ResponseEntity<Void> addAddress(@AuthenticationPrincipal final CustomUserDetails user, final AddressUpsertRequest addressUpsertRequest) {
        memberService.addAddress(user.getId(), addressUpsertRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/address")
    public ResponseEntity<List<AddressResponse>> getAddressList(@AuthenticationPrincipal final CustomUserDetails user) {
        List<AddressResponse> addressResponseList = memberService.getAddressList(user.getId())
                .stream()
                .map(AddressResponse::fromAddress)
                .toList();
        return ResponseEntity.ok(addressResponseList);
    }

    @PostMapping("/address/{addressId}")
    public ResponseEntity<Void> updateAddress(@AuthenticationPrincipal final CustomUserDetails user, @PathVariable final Long addressId, final AddressUpsertRequest addressUpsertRequest) {
        memberService.updateAddress(user.getId(), addressId, addressUpsertRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal final CustomUserDetails user, @PathVariable final Long addressId) {
        memberService.deleteAddress(user.getId(), addressId);
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/carts")
    public ResponseEntity<Void> deleteFromCart(@RequestBody final CartDeletionRequest cartDeletionRequest, @AuthenticationPrincipal final CustomUserDetails user) {
        memberService.deleteFromCart(user.getId(), cartDeletionRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    @PutMapping("/carts/{itemId}")
    public ResponseEntity<Void> updateItemAmountInCart(@PathVariable final long itemId, @AuthenticationPrincipal final CustomUserDetails user, final CartUpdateRequest cartUpdateRequest) {
        memberService.updateItemAmountInCart(user.getId(), itemId, cartUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/register/mailSend")
    public ResponseEntity<Void> sendVerificationCodeToRegister(@RequestBody @Valid MailRequest mailRequest) {
        emailService.sendVerificationCodeToRegister(mailRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> checkVerificationCodeToRegister(final EmailCheckDTO emailCheckDTO) {
        boolean checked = emailService.checkVerificationCode(emailCheckDTO.getEmail(), emailCheckDTO.getVerificationCode());
        if (checked) {
            return ResponseEntity.ok().build();
        }
        throw MailException.getInstance(VALIDATING);
    }
}
