package com.ttarum.member.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.exception.ItemNotFoundException;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.*;
import com.ttarum.member.dto.request.AddressUpsertRequest;
import com.ttarum.member.dto.request.CartAdditionRequest;
import com.ttarum.member.dto.request.CartDeletionRequest;
import com.ttarum.member.dto.request.CartUpdateRequest;
import com.ttarum.member.dto.response.CartResponse;
import com.ttarum.member.dto.response.WishlistResponse;
import com.ttarum.member.exception.*;
import com.ttarum.member.repository.*;
import com.ttarum.s3.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final WishlistRepository wishlistRepository;
    private final NormalMemberRepository normalMemberRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final ImageService imageService;

    /**
     * 일반 회원의 회원가입 메서드
     *
     * @param member       회원
     * @param normalMember 일반 회원
     * @throws MemberException 유효성 검사에 실패한 경우 발생합니다.
     */
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
        if (password.length() > 20 || password.length() < 8) {
            return false;
        }

        List<Character> list = new ArrayList<>();
        for (char c : password.toCharArray()) {
            list.add(c);
        }

        if (!list.removeIf(Character::isDigit))
            return false;
        if (!list.removeIf(Character::isAlphabetic))
            return false;
        boolean containsSpecialCharacter = list.removeIf(c -> c == '~' || c == '!' || c == '@' || c == '#' || c == '$' || c == '%' || c == '^' ||
                c == '&' || c == '*' || c == '-' || c == '_' || c == '=' || c == '+' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' ||
                c == '?' || c == ';' || c == ':');
        return containsSpecialCharacter && list.isEmpty();
    }

    public boolean isNicknameDuplicate(final String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    public boolean isLoginIdDuplicate(final String loginId) {
        return normalMemberRepository.findNormalMemberByLoginId(loginId).isPresent();
    }

    /**
     * 회원을 탈퇴합니다.
     *
     * @param memberId 회원의 Id 값
     * @throws MemberNotFoundException 해당 회원이 존재하지 않으면 발생합니다.
     */
    @Transactional
    public void withdraw(final Long memberId) {
        Member member = getMemberById(memberId);
        member.setIsDeleted(true);
    }

    /**
     * 특정 회원의 프로필 이미지를 업데이트합니다.
     *
     * @param memberId 회원의 Id 값
     * @param image    업데이트할 이미지 파일
     * @throws MemberException 프로필 이미지 업데이트를 실패한 경우 발생합니다.
     */
    @Transactional
    public void updateProfileImage(final Long memberId, final MultipartFile image) {
        Member member = getMemberById(memberId);
        try {
            URL url = imageService.saveImage(image);
            member.setImageUrl(url.toString());
        } catch (IOException e) {
            throw new MemberException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 제품을 찜 목록에 추가합니다.
     *
     * @param memberId 회원의 Id 값
     * @param itemId   찜 목록에 추가될 제품의 Id 값
     * @throws DuplicatedWishlistException 이미 찜 목록에 제품이 존재하는 경우 발생합니다.
     * @throws MemberNotFoundException     해당 회원이 존재하지 않으면 발생합니다.
     * @throws ItemNotFoundException       해당 제품이 존재하지 않으면 발생합니다.
     */
    @Transactional
    public void wishItem(final long memberId, final long itemId) {
        validateDuplicatedWishlist(memberId, itemId);
        Member member = getMemberById(memberId);
        Item item = getItemById(itemId);
        Wishlist wishlist = Wishlist.builder()
                .member(member)
                .item(item)
                .build();
        wishlistRepository.save(wishlist);
    }

    private void validateDuplicatedWishlist(final long memberId, final long itemId) {
        Optional<Wishlist> optionalWishlist = wishlistRepository.findById(new WishlistId(memberId, itemId));
        if (optionalWishlist.isPresent()) {
            throw new DuplicatedWishlistException();
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
     * {@link Long memberId}를 Id 값으로 가진 회원의 찜 목록을 반환합니다.
     *
     * @param memberId 회원의 Id 값
     * @param pageable pageable
     * @return 조회된 찜 목록 리스트
     * @throws MemberNotFoundException 사용자가 존재하지 않을 경우 발생한다.
     */
    public WishlistResponse getWishlistResponse(final Long memberId, final Pageable pageable) {
        checkMemberExistence(memberId);
        return new WishlistResponse(wishlistRepository.findItemSummaryByMemberId(memberId, pageable));
    }

    private void checkMemberExistence(final Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new MemberNotFoundException();
        }
    }

    /**
     * {@link Long memberId}를 Id 값으로 가진 회원의 장바구니에 제품을 추가합니다.
     *
     * @param memberId            특정 회원의 Id 값
     * @param cartAdditionRequest 장바구니에 추가될 제품의 이름과 수량이 담긴 객체
     * @throws MemberNotFoundException 해당 사용자가 존재하지 않으면 발생합니다.
     * @throws ItemNotFoundException   해당 제품이 존재하지 않으면 발생합니다.
     */
    @Transactional
    public void addToCart(final Long memberId, final CartAdditionRequest cartAdditionRequest) {
        Member member = getMemberById(memberId);

        Optional<Cart> optionalCart = cartRepository.findById(new CartId(memberId, cartAdditionRequest.getItemId()));
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.addAmount(cartAdditionRequest.getAmount());
            return;
        }

        Item item = getItemById(cartAdditionRequest.getItemId());
        Cart cart = Cart.builder()
                .member(member)
                .item(item)
                .amount(cartAdditionRequest.getAmount())
                .build();
        cartRepository.save(cart);
    }

    /**
     * {@link Long memberId}를 Id 값으로 가진 회원의 장바구니 목록을 반환합니다.
     *
     * @param memberId 회원의 Id 값
     * @return 장바구니에 담긴 제품 목록
     */
    public List<CartResponse> getCartResponseList(final Long memberId) {
        return cartRepository.getCartResponseListByMemberId(memberId);
    }

    /**
     * 사용자의 주소를 추가한다.
     *
     * @param memberId 사용자의 Id 값
     * @param request  주소 추가 요청 객체
     * @throws MemberNotFoundException 해당 사용자가 존재하지 않으면 발생한다.
     */
    @Transactional
    public void addAddress(final Long memberId, final AddressUpsertRequest request) {
        Member member = getMemberById(memberId);

        Address address = Address.builder()
                .member(member)
                .address(request.getAddress())
                .build();

        addressRepository.save(address);
    }

    /**
     * 특정 사용자의 배송지 목록을 조회한다.
     *
     * @param memberId 사용자의 Id 값
     * @return 사용자의 배송지 목록
     */
    public List<Address> getAddressList(final Long memberId) {
        return addressRepository.findByMemberIdOrderByLastUsedAtDesc(memberId);
    }

    private Address getValidAddress(final Long memberId, final Long addressId) throws AddressException {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(AddressException::notFound);
        if (!Objects.equals(memberId, address.getMember().getId())) {
            throw AddressException.noOwner();
        }
        return address;
    }

    /**
     * 특정 사용자의 배송지를 업데이트한다.
     *
     * @param memberId  사용자의 Id 값
     * @param addressId 배송지의 Id 값
     * @param request   업데이트할 배송지의 정보가 담긴 객체
     */
    @Transactional
    public void updateAddress(final Long memberId, final Long addressId, final AddressUpsertRequest request) {
        Address address = getValidAddress(memberId, addressId);
        address.setAddress(request.getAddress());
        addressRepository.save(address);
    }

    /**
     * 특정 사용자 배송지의 최근 사용 일자를 업데이트한다.
     *
     * @param memberId  사용자의 Id 값
     * @param addressId 배송지의 Id 값
     */
    @Transactional
    public void updateLastUsedAt(final Long memberId, final Long addressId) {
        Address address = getValidAddress(memberId, addressId);
        address.updateLastUsedAt();
        addressRepository.save(address);
    }

    /**
     * 특정 사용자의 배송지를 삭제한다.
     *
     * @param memberId  사용자의 Id 값
     * @param addressId 배송지의 Id 값
     */
    @Transactional
    public void deleteAddress(final Long memberId, final Long addressId) {
        Address address = getValidAddress(memberId, addressId);
        addressRepository.delete(address);
    }

    /**
     * 특정 회원의 장바구니에서 특정 제품들을 제거한다.
     *
     * @param memberId            회원의 Id 값
     * @param cartDeletionRequest 제품의 Id 값이 담긴 객체
     */
    @Transactional
    public void deleteFromCart(final long memberId, final CartDeletionRequest cartDeletionRequest) {
        List<CartId> idList = cartDeletionRequest.getItemIdList().stream()
                .map(c -> new CartId(memberId, c))
                .toList();
        cartRepository.deleteAllByIdList(idList);
    }

    /**
     * 특정 회원의 장바구니에 담긴 제품의 수량을 변경한다.
     *
     * @param memberId          사용자의 Id 값
     * @param cartUpdateRequest 변경할 제품의 데이터가 담긴 객체
     */
    @Transactional
    public void updateItemAmountInCart(final long memberId, final long itemId, final CartUpdateRequest cartUpdateRequest) {
        Cart cart = getCartById(new CartId(memberId, itemId));
        cart.updateAmount(cartUpdateRequest.getAmount());
    }

    private Cart getCartById(final CartId cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(CartNotFoundException::new);
    }
}
