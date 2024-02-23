package com.ttarum.member.repository;

import com.ttarum.member.domain.WishList;
import com.ttarum.member.domain.WishListId;
import com.ttarum.member.dto.response.ItemSummaryResponseForWishList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, WishListId> {

    @Query("SELECT wl FROM WishList wl WHERE wl.item.id = :itemId AND wl.member.id = :memberId")
    Optional<WishList> findByMemberIdAndItemId(@Param("memberId") long memberId, @Param("itemId") long itemId);

    /**
     * {@link Long memberId}가 Id 값인 회원의 {@link WishList}를 {@link ItemSummaryResponseForWishList} 리스트로 반환합니다.
     *
     * @param memberId 회원의 Id 값
     * @param pageable pageable
     * @return {@link ItemSummaryResponseForWishList} 리스트
     */
    @Query("""
            SELECT new com.ttarum.member.dto.response.ItemSummaryResponseForWishList(i.id, i.category.name, i.name, i.price, AVG(r.star), i.itemImageUrl, wl.createdAt)
            FROM WishList wl
            LEFT JOIN FETCH Item i
            ON wl.item.id = i.id
            LEFT JOIN FETCH Review r
            ON r.item.id = i.id
            WHERE wl.member.id = :memberId
            GROUP BY i.id, i.category.name, i.name, i.price, i.itemImageUrl, wl.createdAt
            """)
    List<ItemSummaryResponseForWishList> findItemSummaryByMemberId(@Param("memberId") long memberId, Pageable pageable);
}

