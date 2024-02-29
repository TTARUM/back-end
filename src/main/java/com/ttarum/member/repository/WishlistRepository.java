package com.ttarum.member.repository;

import com.ttarum.member.domain.Wishlist;
import com.ttarum.member.domain.WishlistId;
import com.ttarum.member.dto.response.ItemSummaryResponseForWishlist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {

    /**
     * {@link Long memberId}가 Id 값인 회원의 {@link Wishlist}를 {@link ItemSummaryResponseForWishlist} 리스트로 반환합니다.
     *
     * @param memberId 회원의 Id 값
     * @param pageable pageable
     * @return {@link ItemSummaryResponseForWishlist} 리스트
     */
    @Query("""
            SELECT new com.ttarum.member.dto.response.ItemSummaryResponseForWishlist(i.id, i.category.name, i.name, i.price, AVG(r.star), i.itemImageUrl, wl.createdAt)
            FROM Wishlist wl
            LEFT JOIN FETCH Item i
            ON wl.item.id = i.id
            LEFT JOIN FETCH Review r
            ON r.item.id = i.id
            WHERE wl.member.id = :memberId
            GROUP BY i.id, i.category.name, i.name, i.price, i.itemImageUrl, wl.createdAt
            """)
    List<ItemSummaryResponseForWishlist> findItemSummaryByMemberId(@Param("memberId") long memberId, Pageable pageable);
}

