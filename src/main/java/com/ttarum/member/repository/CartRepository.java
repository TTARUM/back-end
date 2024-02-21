package com.ttarum.member.repository;

import com.ttarum.member.domain.Cart;
import com.ttarum.member.domain.CartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, CartId> {

    @Query("SELECT c FROM Cart c WHERE c.member.id = :memberId AND c.item.id = :itemId")
    Optional<Cart> findByMemberIdAndItemId(@Param("memberId") long memberId, @Param("itemId") long itemId);
}
