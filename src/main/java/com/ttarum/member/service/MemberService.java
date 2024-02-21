package com.ttarum.member.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.exception.ItemNotFoundException;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.NormalMember;
import com.ttarum.member.domain.WishList;
import com.ttarum.member.exception.DuplicatedWishListException;
import com.ttarum.member.dto.response.WishListResponse;
import com.ttarum.member.exception.MemberException;
import com.ttarum.member.exception.MemberNotFoundException;
import com.ttarum.member.repository.MemberRepository;
import com.ttarum.member.repository.NormalMemberRepository;
import com.ttarum.member.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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
        normalMember.setMember(saved);
        normalMember.encodePassword(passwordEncoder);
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

    /**
     * 특정 제품을 찜 목록에 추가한다.
     *
     * @param memberId 사용자의 Id 값
     * @param itemId   찜 목록에 추가될 제품의 Id 값
     * @throws DuplicatedWishListException 이미 찜 목록에 제품이 존재하는 경우 발생한다.
     * @throws MemberNotFoundException     해당 사용자가 존재하지 않으면 발생한다.
     * @throws ItemNotFoundException       해당 제품이 존재하지 않으면 발생한다.
     */
    @Transactional
    public void wishItem(final long memberId, final long itemId) {
        validateDuplicatedWishList(memberId, itemId);
        Member member = getMemberById(memberId);
        Item item = getItemById(itemId);
        WishList wishList = WishList.builder()
                .member(member)
                .item(item)
                .build();
        wishListRepository.save(wishList);
    }

    private void validateDuplicatedWishList(final long memberId, final long itemId) {
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

    /**
     * 찜 목록 조회 메서드
     *
     * @param memberId 사용자의 Id 값
     * @param pageable pageable
     * @return 조회된 찜 목록  리스트
     * @throws MemberNotFoundException 사용자가 존재하지 않을 경우 발생한다.
     */
    public WishListResponse getWishListResponse(final Long memberId, final Pageable pageable) {
        checkMemberExistence(memberId);
        return new WishListResponse(wishListRepository.findItemSummaryByMemberId(memberId, pageable));
    }

    private void checkMemberExistence(final Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new MemberNotFoundException();
        }
    }
}
