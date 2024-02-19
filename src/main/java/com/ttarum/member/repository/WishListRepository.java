package com.ttarum.member.repository;

import com.ttarum.member.domain.WishList;
import com.ttarum.member.domain.WishListId;
import com.ttarum.member.dto.response.ItemSummaryResponseForWishList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, WishListId> {


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

