package com.ttarum.member.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.member.dto.request.AddressUpsertRequest;
import com.ttarum.member.dto.request.CartAdditionRequest;
import com.ttarum.member.dto.request.CartUpdateRequest;
import com.ttarum.member.dto.request.NormalMemberRegister;
import com.ttarum.member.dto.response.CartResponse;
import com.ttarum.member.dto.response.WishListResponse;
import com.ttarum.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberControllerImpl implements MemberController {


    private static final Integer DEFAULT_WISH_LIST_SIZE = 8;

    private final MemberService memberService;

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
    @PostMapping("/wish-item")
    public ResponseEntity<Void> wishItem(@AuthenticationPrincipal final CustomUserDetails user, @RequestParam final long itemId) {
        memberService.wishItem(user.getId(), itemId);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/wish-item")
    public ResponseEntity<WishListResponse> getWishList(@AuthenticationPrincipal final CustomUserDetails user,
                                                        final Optional<Integer> page,
                                                        final Optional<Integer> size) {
        PageRequest pageRequest = PageRequest.of(page.orElse(0), size.orElse(DEFAULT_WISH_LIST_SIZE));
        WishListResponse wishListResponse = memberService.getWishListResponse(user.getId(), pageRequest);
        return ResponseEntity.ok(wishListResponse);
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

    @PostMapping("/address")
    public ResponseEntity<Void> addAddress(@AuthenticationPrincipal final CustomUserDetails user, final AddressUpsertRequest addressUpsertRequest) {
        memberService.addAddress(user.getId(), addressUpsertRequest);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> deleteFromCart(@RequestParam final long itemId, @AuthenticationPrincipal final CustomUserDetails user) {
        memberService.deleteFromCart(user.getId(), itemId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PutMapping("/carts")
    public ResponseEntity<Void> updateItemAmountInCart(@AuthenticationPrincipal final CustomUserDetails user, final CartUpdateRequest cartUpdateRequest) {
        memberService.updateItemAmountInCart(user.getId(), cartUpdateRequest);
        return ResponseEntity.ok().build();
    }
}
