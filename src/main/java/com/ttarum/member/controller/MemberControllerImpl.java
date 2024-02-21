package com.ttarum.member.controller;

import com.ttarum.auth.domain.CustomUserDetails;
import com.ttarum.member.dto.request.CartAdditionRequest;
import com.ttarum.member.dto.request.NormalMemberRegister;
import com.ttarum.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberControllerImpl implements MemberController {

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
    @PostMapping("/carts")
    public ResponseEntity<Void> addToCart(@AuthenticationPrincipal final CustomUserDetails user, final CartAdditionRequest cartAdditionRequest) {
        memberService.addToCart(user.getId(), cartAdditionRequest);
        return ResponseEntity.ok().build();
    }
}
