package com.ttarum.member.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.*;
import com.ttarum.member.dto.request.AddressUpsertRequest;
import com.ttarum.member.dto.request.CartDeletionRequest;
import com.ttarum.member.dto.request.CartUpdateRequest;
import com.ttarum.member.dto.response.CartResponse;
import com.ttarum.member.dto.response.ItemSummaryResponseForWishlist;
import com.ttarum.member.dto.response.WishlistResponse;
import com.ttarum.member.exception.CartNotFoundException;
import com.ttarum.member.exception.DuplicatedWishlistException;
import com.ttarum.member.exception.MemberException;
import com.ttarum.member.exception.MemberNotFoundException;
import com.ttarum.member.repository.*;
import com.ttarum.s3.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private NormalMemberRepository normalMemberRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private WishlistRepository wishlistRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ImageService imageService;

    @Test
    @DisplayName("일반 유저 회원가입 - happy path")
    void registerNormalUser() {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("1234qwer!@")
                .email("testEmail@gmail.com")
                .build();

        when(passwordEncoder.encode(any())).thenReturn(any());

        // when
        assertDoesNotThrow(() -> memberService.registerNormalUser(targetMember, targetNormalMember));

        // then
        verify(memberRepository, times(1)).save(targetMember);
        verify(normalMemberRepository, times(1)).save(targetNormalMember);
    }

    @ParameterizedTest
    @DisplayName("일반 유저 회원가입 - 유효하지 않은 닉네임으로 가입 시 예외가 발생한다.")
    @ValueSource(strings = {"", "tooLongNickname"})
    void registerNormalUser_registerWithInvalidNicknameThrowException(String nickname) {
        // given
        Member targetMember = Member.builder()
                .nickname(nickname)
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .email("testEmail@gmail.com")
                .build();

        // when
        Exception exception = assertThrows(MemberException.class, () -> {
            memberService.registerNormalUser(targetMember, targetNormalMember);
        });

        // then
        assertTrue(exception.getMessage().contains("올바르지 않은 닉네임입니다."));
    }

    @ParameterizedTest
    @DisplayName("일반 유저 회원가입 - 유효하지 않은 이메일로 가입 시 예외가 발생한다.")
    @ValueSource(strings = {"", "noDomain", "noAtSign.com", "noDot@com"})
    void registerNormalUser_registerWithInvalidEmailThrowException(String email) {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("testPassword123")
                .email(email)
                .build();

        // when
        Exception exception = assertThrows(MemberException.class, () -> {
            memberService.registerNormalUser(targetMember, targetNormalMember);
        });

        // then
        assertTrue(exception.getMessage().contains("올바르지 않은 이메일입니다."));
    }

    @ParameterizedTest
    @DisplayName("일반 유저 회원가입 - 유효하지 않은 비밀번호로 가입 시 예외가 발생한다.")
    @ValueSource(strings = {"", "short", "noNumber", "12341234"})
    void registerNormalUser_registerWithInvalidPasswordThrowException(String password) {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password(password)
                .email("testEmail@gmail.com")
                .build();

        // when
        Exception exception = assertThrows(MemberException.class, () -> {
            memberService.registerNormalUser(targetMember, targetNormalMember);
        });

        // then
        assertTrue(exception.getMessage().contains("올바르지 않은 비밀번호입니다."));
    }

    @Test
    @DisplayName("일반 유저 회원가입 - 닉네임 중복 시 예외가 발생한다.")
    void registerNormalUser_registerWithDuplicateNicknameThrowException() {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("1234qwer!@")
                .email("testEmail@gmail.com")
                .build();

        when(memberRepository.findByNickname(anyString())).thenReturn(Optional.of(targetMember));

        // when
        Exception exception = assertThrows(MemberException.class, () -> {
            memberService.registerNormalUser(targetMember, targetNormalMember);
        });

        // then
        assertTrue(exception.getMessage().contains("닉네임이 중복되었습니다."));
    }

    @Test
    @DisplayName("일반 유저 회원가입 - 로그인 아이디 중복 시 예외가 발생한다.")
    void registerNormalUser_registerWithDuplicateLoginIdThrowException() {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId("testLoginId")
                .password("1234qwer!@")
                .email("testEmail@gmail.com")
                .build();

        when(normalMemberRepository.findNormalMemberByLoginId("testLoginId")).thenReturn(Optional.of(targetNormalMember));

        // when
        Exception exception = assertThrows(MemberException.class, () -> {
            memberService.registerNormalUser(targetMember, targetNormalMember);
        });

        // then
        assertTrue(exception.getMessage().contains("로그인 아이디가 중복되었습니다."));
    }

    @ParameterizedTest
    @DisplayName("일반 유저 회원가입 - 로그인 아이디 길이 불만족 시 예외가 발생한다.")
    @ValueSource(strings = {"", "asdf", "asdfasdfasdfasdfasdfa"})
    void registerNormalUser_registerWithInvalidLoginIdThrowException(final String input) {
        // given
        Member targetMember = Member.builder()
                .nickname("nickname1")
                .name("testName")
                .phoneNumber("testPhoneNumber")
                .build();
        NormalMember targetNormalMember = NormalMember.builder()
                .loginId(input)
                .password("1234qwer!@")
                .email("testEmail@gmail.com")
                .build();

        // when
        Exception exception = assertThrows(MemberException.class, () ->
                memberService.registerNormalUser(targetMember, targetNormalMember)
        );

        // then
        assertTrue(exception.getMessage().contains("올바르지 않은 로그인 아이디입니다."));
    }

    @Test
    @DisplayName("회원 탈퇴 - happy path")
    void deleteMember() {
        // given
        long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        memberService.withdraw(memberId);

        // then
        assertTrue(member.getIsDeleted());
    }

    @Test
    @DisplayName("회원 프로필 이미지 업데이트 - happy path")
    void updateProfileImage() {
        // given
        long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .build();
        MultipartFile image = new MockMultipartFile("image", "test".getBytes());

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        assertDoesNotThrow(
                () -> when(imageService.saveImage(any(MultipartFile.class))).thenReturn(new URL("http://test.com"))
        );

        // when
        memberService.updateProfileImage(memberId, image);

        // then
        assertEquals("http://test.com", member.getImageUrl());
    }

    @Test
    @DisplayName("제품 찜 하기")
    void wishItem() {
        // given
        Member member = Member.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .build();
        Wishlist wishlist = Wishlist.builder()
                .member(member)
                .item(item)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // when
        memberService.wishItem(member.getId(), item.getId());

        // then
        verify(memberRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findById(1L);
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
        assertThat(wishlist.getItem().getId()).isEqualTo(item.getId());
        assertThat(wishlist.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("제품 찜 하기 - 이미 찜 목록에 존재하는 제품을 찜할 경우 예외가 발생한다.")
    void wishItemFailedByDuplicatedWishlist() {
        // given
        long memberId = 1L;
        long itemId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Item item = Item.builder()
                .id(itemId)
                .build();
        Wishlist wishlist = Wishlist.builder()
                .member(member)
                .item(item)
                .build();

        when(wishlistRepository.findById(new WishlistId(memberId, itemId))).thenReturn(Optional.of(wishlist));

        // when & then
        assertThatThrownBy(() -> memberService.wishItem(memberId, itemId))
                .isInstanceOf(DuplicatedWishlistException.class);
    }

    @Test
    @DisplayName("찜 목록을 조회할 수 있다.")
    void getWishlistResponse() {
        // given
        long memberId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 8);
        Member member = Member.builder()
                .id(memberId)
                .build();
        Instant now = Instant.now();
        List<ItemSummaryResponseForWishlist> response = List.of(
                ItemSummaryResponseForWishlist.builder()
                        .itemId(1L)
                        .name("item1")
                        .categoryName("category1")
                        .price(1000)
                        .rating(4.5)
                        .imageUrl("url1")
                        .createdAt(now)
                        .build(),
                ItemSummaryResponseForWishlist.builder()
                        .itemId(2L)
                        .name("item2")
                        .categoryName("category2")
                        .price(2000)
                        .rating(5.0)
                        .imageUrl("url2")
                        .createdAt(now)
                        .build()
        );

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(wishlistRepository.findItemSummaryByMemberId(memberId, pageRequest)).thenReturn(response);

        // when
        WishlistResponse wishlistResponse = memberService.getWishlistResponse(memberId, pageRequest);

        // then
        verify(memberRepository, times(1)).findById(memberId);
        verify(wishlistRepository, times(1)).findItemSummaryByMemberId(memberId, pageRequest);

        assertThat(wishlistResponse.getWishlist()).size().isEqualTo(2);
        assertThat(wishlistResponse.getWishlist().get(0).getItemId()).isEqualTo(1L);
        assertThat(wishlistResponse.getWishlist().get(0).getName()).isEqualTo("item1");
        assertThat(wishlistResponse.getWishlist().get(0).getCategoryName()).isEqualTo("category1");
        assertThat(wishlistResponse.getWishlist().get(0).getPrice()).isEqualTo(1000);
        assertThat(wishlistResponse.getWishlist().get(0).getRating()).isEqualTo(4.5);
        assertThat(wishlistResponse.getWishlist().get(0).getImageUrl()).isEqualTo("url1");
        assertThat(wishlistResponse.getWishlist().get(0).getCreatedAt()).isEqualTo(now);

        assertThat(wishlistResponse.getWishlist().get(1).getItemId()).isEqualTo(2L);
        assertThat(wishlistResponse.getWishlist().get(1).getName()).isEqualTo("item2");
        assertThat(wishlistResponse.getWishlist().get(1).getCategoryName()).isEqualTo("category2");
        assertThat(wishlistResponse.getWishlist().get(1).getPrice()).isEqualTo(2000);
        assertThat(wishlistResponse.getWishlist().get(1).getRating()).isEqualTo(5.0);
        assertThat(wishlistResponse.getWishlist().get(1).getImageUrl()).isEqualTo("url2");
        assertThat(wishlistResponse.getWishlist().get(1).getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("찜 목록을 조회할 수 있다. - 유저가 존재하지 않을 시 예외가 발생한다.")
    void getWishlistResponseFailedByInvalidMember() {
        // given
        long memberId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 8);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getWishlistResponse(memberId, pageRequest))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("장바구니를 조회할 수 있다.")
    void getCartResponseList() {
        // given
        long memberId = 1L;
        List<CartResponse> cartResponseList = List.of(
                CartResponse.builder()
                        .itemId(1L)
                        .itemName("item1")
                        .categoryName("category1")
                        .itemImageUrl("image1")
                        .price(13000)
                        .amount(1)
                        .build(),
                CartResponse.builder()
                        .itemId(2L)
                        .itemName("item2")
                        .categoryName("category2")
                        .itemImageUrl("image2")
                        .price(23000)
                        .amount(2)
                        .build()
        );

        when(cartRepository.getCartResponseListByMemberId(memberId)).thenReturn(cartResponseList);

        // when
        List<CartResponse> result = memberService.getCartResponseList(memberId);

        // then
        verify(cartRepository, times(1)).getCartResponseListByMemberId(memberId);
        assertThat(result).size().isEqualTo(2);
    }

    @Test
    @DisplayName("배송지 추가 - happy path")
    void addAddress() {
        // given
        Long memberId = 1L;
        Member dummyMember = Member.builder().build();

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(dummyMember));

        // when
        memberService.addAddress(memberId, new AddressUpsertRequest("test address"));

        // then
        verify(addressRepository).save(
                Address.builder()
                        .member(dummyMember)
                        .address("test address")
                        .build()
        );
    }

    @Test
    @DisplayName("배송지 수정 - happy path")
    void updateAddress() {
        // given
        Long memberId = 1L;
        Long addressId = 999L;
        Member testMember = Member.builder().id(memberId).build();
        Address previousAddress = Address.builder().member(testMember).build();

        when(addressRepository.findById(addressId))
                .thenReturn(Optional.of(previousAddress));
        // when
        memberService.updateAddress(memberId, addressId, new AddressUpsertRequest("new address"));

        // then
        verify(addressRepository).save(
                Address.builder()
                        .member(testMember)
                        .address("new address")
                        .build()
        );
    }

    @Test
    @DisplayName("배송지 삭제 - happy path")
    void deleteAddress() {
        // given
        Long memberId = 1L;
        Long addressId = 999L;
        Member testMember = Member.builder().id(memberId).build();
        Address targetAddress = Address.builder().member(testMember).build();

        when(addressRepository.findById(addressId))
                .thenReturn(Optional.of(targetAddress));
        // when
        memberService.deleteAddress(memberId, addressId);

        // then
        verify(addressRepository).delete(targetAddress);
    }

    @Test
    @DisplayName("장바구니에서 제품을 제거할 수 있다.")
    void deleteFromCart() {
        // given
        long memberId = 1;
        long itemId = 1;
        CartDeletionRequest cartDeletionRequest = new CartDeletionRequest(List.of(itemId));

        doNothing().when(cartRepository).deleteAllByIdList(any());

        // when
        memberService.deleteFromCart(memberId, cartDeletionRequest);

        // then
        verify(cartRepository, times(1)).deleteAllByIdList(any());
    }

    @Test
    @DisplayName("장바구니에 있는 제품의 수량을 변경할 수 있다.")
    void updateItemAmountInCart() {
        // given
        long memberId = 1;
        long itemId = 1;
        int amount = 3;
        CartUpdateRequest cartUpdateRequest = new CartUpdateRequest(amount);
        Cart cart = Cart.builder()
                .member(Member.builder().id(memberId).build())
                .item(Item.builder().id(itemId).build())
                .amount(1)
                .build();

        when(cartRepository.findById(new CartId(memberId, itemId))).thenReturn(Optional.of(cart));

        // when
        memberService.updateItemAmountInCart(memberId, itemId, cartUpdateRequest);

        // then
        verify(cartRepository, times(1)).findById(any());
        assertThat(cart.getAmount()).isEqualTo(amount);
        assertThat(cart.getMember().getId()).isEqualTo(memberId);
        assertThat(cart.getItem().getId()).isEqualTo(itemId);
    }

    @Test
    @DisplayName("장바구니에 없는 제품의 수량을 변경하는 경우 예외가 발생한다.")
    void updateItemAmountInCartFailedByInvalidItem() {
        // given
        long memberId = 1;
        long itemId = 1;
        int amount = 3;
        CartUpdateRequest cartUpdateRequest = new CartUpdateRequest(amount);

        when(cartRepository.findById(new CartId(memberId, itemId))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.updateItemAmountInCart(memberId, itemId, cartUpdateRequest))
                .isInstanceOf(CartNotFoundException.class);
    }
}
