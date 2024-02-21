package com.ttarum.member.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.member.dto.request.NormalMemberRegister;
import com.ttarum.member.dto.response.WishListResponse;
import com.ttarum.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public void registerNormalMember(NormalMemberRegister dto) {
        memberService.registerNormalUser(dto.toMemberEntity(), dto.toNormalMemberEntity());
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
}
