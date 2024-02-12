package com.ttarum.member.controller;

import com.ttarum.member.dto.request.NormalMemberRegister;
import com.ttarum.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
