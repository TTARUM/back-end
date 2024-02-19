package com.ttarum.member.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.exception.ItemNotFoundException;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.NormalMember;
import com.ttarum.member.domain.WishList;
import com.ttarum.member.exception.DuplicatedWishListException;
import com.ttarum.member.exception.MemberException;
import com.ttarum.member.exception.MemberNotFoundException;
import com.ttarum.member.repository.MemberRepository;
import com.ttarum.member.repository.NormalMemberRepository;
import com.ttarum.member.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final WishListRepository wishListRepository;
    private final NormalMemberRepository normalMemberRepository;

    @Transactional
    public void registerNormalUser(Member member, NormalMember normalMember) throws MemberException {
        if (!isValidNickname(member.getNickname())) {
            throw new MemberException(HttpStatus.BAD_REQUEST, "올바르지 않은 닉네임입니다.");
        }
        if (!isValidEmail(normalMember.getEmail())) {
            throw new MemberException(HttpStatus.BAD_REQUEST, "올바르지 않은 이메일입니다.");
        }
        if (!isValidPassword(normalMember.getPassword())) {
            throw new MemberException(HttpStatus.BAD_REQUEST, "올바르지 않은 비밀번호입니다.");
        }
        if (isNicknameDuplicate(member.getNickname())) {
            throw new MemberException(HttpStatus.BAD_REQUEST, "닉네임이 중복되었습니다.");
        }
        if (isLoginIdDuplicate(normalMember.getLoginId())) {
            throw new MemberException(HttpStatus.BAD_REQUEST, "로그인 아이디가 중복되었습니다.");
        }
        Member saved = memberRepository.save(member);
        // TODO: Before save the password to DB, encrypt it first
        normalMember.setMember(saved);
        normalMemberRepository.save(normalMember);
    }

    private boolean isValidNickname(final String nickname) {
        return !nickname.isEmpty() && nickname.length() <= 10;
    }

    private boolean isValidEmail(final String email) {
        if (email.isEmpty() || email.length() > 320) {
            return false;
        }
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    private boolean isValidPassword(final String password) {
        if (password.isEmpty() || password.length() > 20) {
            return false;
        }
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }

    public boolean isNicknameDuplicate(final String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    public boolean isLoginIdDuplicate(final String loginId) {
        return normalMemberRepository.findNormalMemberByLoginId(loginId).isPresent();
    }

    @Transactional
    public void wishItem(final long memberId, final long itemId) {
        checkWishListExistence(memberId, itemId);
        Member member = getMemberById(memberId);
        Item item = getItemById(itemId);
        WishList wishList = WishList.builder()
                .member(member)
                .item(item)
                .build();
        wishListRepository.save(wishList);
    }

    private void checkWishListExistence(final long memberId, final long itemId) {
        Optional<WishList> optionalWishList = wishListRepository.findByMemberIdAndItemId(memberId, itemId);
        if (optionalWishList.isPresent()) {
            throw new DuplicatedWishListException();
        }
    }

    private Member getMemberById(final long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Item getItemById(final long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);
    }
}
