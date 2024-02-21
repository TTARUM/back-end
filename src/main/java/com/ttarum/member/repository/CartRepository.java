package com.ttarum.member.repository;

import com.ttarum.member.domain.Cart;
import com.ttarum.member.domain.CartId;
import com.ttarum.member.dto.response.CartResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, CartId> {

    @Query("""
            SELECT new com.ttarum.member.dto.response.CartResponse(i.id, i.name, i.category.name, i.itemImageUrl, i.price)
            FROM Cart c
            LEFT JOIN FETCH Item i
            ON c.item.id = i.id
            WHERE c.member.id = :memberId
            """)
    List<CartResponse> getCartResponseListByMemberId(@Param("memberId") long memberId);
}
