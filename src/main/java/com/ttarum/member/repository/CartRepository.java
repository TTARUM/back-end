package com.ttarum.member.repository;

import com.ttarum.member.domain.Cart;
import com.ttarum.member.domain.CartId;
import com.ttarum.member.dto.response.CartResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, CartId> {

    @Query("SELECT c FROM Cart c WHERE c.member.id = :memberId AND c.item.id = :itemId")
    Optional<Cart> findByMemberIdAndItemId(@Param("memberId") long memberId, @Param("itemId") long itemId);

    /**
     * {@link Long memberId}가 Id 값인 회원의 모든 {@link Cart}를 {@link CartResponse} 리스트로 반환합니다.
     *
     * @param memberId 회원의 Id 값
     * @return {@link CartResponse} 리스트
     */
    @Query("""
            SELECT new com.ttarum.member.dto.response.CartResponse(i.id, i.name, i.category.name, i.itemImageUrl, i.price, c.amount)
            FROM Cart c
            LEFT JOIN FETCH Item i
            ON c.item.id = i.id
            WHERE c.member.id = :memberId
            """)
    List<CartResponse> getCartResponseListByMemberId(@Param("memberId") long memberId);
}
