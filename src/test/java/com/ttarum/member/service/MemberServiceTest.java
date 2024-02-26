package com.ttarum.member.service;

import com.ttarum.item.domain.Item;
import com.ttarum.item.repository.ItemRepository;
import com.ttarum.member.domain.Address;
import com.ttarum.member.domain.Member;
import com.ttarum.member.domain.NormalMember;
import com.ttarum.member.domain.WishList;
import com.ttarum.member.dto.request.AddressUpsertRequest;
import com.ttarum.member.dto.response.CartResponse;
import com.ttarum.member.dto.response.ItemSummaryResponseForWishList;
import com.ttarum.member.dto.response.WishListResponse;
import com.ttarum.member.exception.DuplicatedWishListException;
import com.ttarum.member.exception.MemberException;
import com.ttarum.member.exception.MemberNotFoundException;
import com.ttarum.member.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private WishListRepository wishListRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AddressRepository addressRepository;

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
                .password("testPassword123")
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
                .password("testPassword123")
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
                .password("testPassword123")
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
        WishList wishList = WishList.builder()
                .member(member)
                .item(item)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(wishListRepository.save(any(WishList.class))).thenReturn(wishList);

        // when
        memberService.wishItem(member.getId(), item.getId());

        // then
        verify(memberRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findById(1L);
        verify(wishListRepository, times(1)).save(any(WishList.class));
        assertThat(wishList.getItem().getId()).isEqualTo(item.getId());
        assertThat(wishList.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("제품 찜 하기 - 이미 찜 목록에 존재하는 제품을 찜할 경우 예외가 발생한다.")
    void wishItemFailedByDuplicatedWishList() {
        // given
        long memberId = 1L;
        long itemId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Item item = Item.builder()
                .id(itemId)
                .build();
        WishList wishList = WishList.builder()
                .member(member)
                .item(item)
                .build();

        when(wishListRepository.findByMemberIdAndItemId(memberId, itemId)).thenReturn(Optional.of(wishList));

        // when & then
        assertThatThrownBy(() -> memberService.wishItem(memberId, itemId))
                .isInstanceOf(DuplicatedWishListException.class);
    }

    @Test
    @DisplayName("찜 목록을 조회할 수 있다.")
    void getWishListResponse() {
        // given
        long memberId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 8);
        Member member = Member.builder()
                .id(memberId)
                .build();
        Instant now = Instant.now();
        List<ItemSummaryResponseForWishList> response = List.of(
                ItemSummaryResponseForWishList.builder()
                        .itemId(1L)
                        .name("item1")
                        .categoryName("category1")
                        .price(1000)
                        .rating(4.5)
                        .imageUrl("url1")
                        .createdAt(now)
                        .build(),
                ItemSummaryResponseForWishList.builder()
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
        when(wishListRepository.findItemSummaryByMemberId(memberId, pageRequest)).thenReturn(response);

        // when
        WishListResponse wishListResponse = memberService.getWishListResponse(memberId, pageRequest);

        // then
        verify(memberRepository, times(1)).findById(memberId);
        verify(wishListRepository, times(1)).findItemSummaryByMemberId(memberId, pageRequest);

        assertThat(wishListResponse.getWishList()).size().isEqualTo(2);
        assertThat(wishListResponse.getWishList().get(0).getItemId()).isEqualTo(1L);
        assertThat(wishListResponse.getWishList().get(0).getName()).isEqualTo("item1");
        assertThat(wishListResponse.getWishList().get(0).getCategoryName()).isEqualTo("category1");
        assertThat(wishListResponse.getWishList().get(0).getPrice()).isEqualTo(1000);
        assertThat(wishListResponse.getWishList().get(0).getRating()).isEqualTo(4.5);
        assertThat(wishListResponse.getWishList().get(0).getImageUrl()).isEqualTo("url1");
        assertThat(wishListResponse.getWishList().get(0).getCreatedAt()).isEqualTo(now);

        assertThat(wishListResponse.getWishList().get(1).getItemId()).isEqualTo(2L);
        assertThat(wishListResponse.getWishList().get(1).getName()).isEqualTo("item2");
        assertThat(wishListResponse.getWishList().get(1).getCategoryName()).isEqualTo("category2");
        assertThat(wishListResponse.getWishList().get(1).getPrice()).isEqualTo(2000);
        assertThat(wishListResponse.getWishList().get(1).getRating()).isEqualTo(5.0);
        assertThat(wishListResponse.getWishList().get(1).getImageUrl()).isEqualTo("url2");
        assertThat(wishListResponse.getWishList().get(1).getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("찜 목록을 조회할 수 있다. - 유저가 존재하지 않을 시 예외가 발생한다.")
    void getWishListResponseFailedByInvalidMember() {
        // given
        long memberId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 8);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getWishListResponse(memberId, pageRequest))
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
    @DisplayName("배송지 최근 사용 일자 업데이트 - happy path")
    void updateLastUsedAt() {
        // given
        Long memberId = 1L;
        Long addressId = 999L;
        Member testMember = Member.builder().id(memberId).build();
        Address previousAddress = Address.builder().member(testMember).build();

        when(addressRepository.findById(addressId))
                .thenReturn(Optional.of(previousAddress));
        // when
        memberService.updateLastUsedAt(memberId, addressId);

        // then
        verify(addressRepository).save(
                Address.builder()
                        .member(testMember)
                        .address("new address")
                        .lastUsedAt(any())
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
}
