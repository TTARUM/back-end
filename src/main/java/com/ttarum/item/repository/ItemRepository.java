package com.ttarum.item.repository;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemSummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
            SELECT new com.ttarum.item.dto.response.ItemSummaryResponse(i.id, i.category.name, i.name, i.price, AVG(r.star), i.itemImageUrl, false, i.createdAt, COUNT(oi.order.id))
            FROM Item i
            LEFT JOIN FETCH Review r
            ON r.item.id = i.id
            LEFT JOIN FETCH OrderItem oi
            ON oi.item.id = i.id
            WHERE i.name LIKE %:query%
            GROUP BY i.id, i.category.name, i.name, i.price, i.itemImageUrl, i.createdAt
            """)
    List<ItemSummaryResponse> getItemSummaryListByName(@Param("query") String query, Pageable pageable);

    @Query("""
            SELECT new com.ttarum.item.dto.response.ItemSummaryResponse(i.id, i.category.name, i.name, i.price, AVG(r.star), i.itemImageUrl, (COUNT(wl.id) > 0), i.createdAt, COUNT(oi.order.id))
            FROM Item i
            LEFT JOIN FETCH Review r
            ON r.item.id = i.id
            LEFT JOIN FETCH OrderItem oi
            ON oi.item.id = i.id
            LEFT JOIN FETCH WishList wl
            ON wl.item.id = i.id AND wl.member.id = :memberId
            WHERE i.name LIKE %:query%
            GROUP BY i.id, i.category.name, i.name, i.price, i.itemImageUrl, i.createdAt
            """)
    List<ItemSummaryResponse> getItemSummaryListByName(@Param("query") String query, Pageable pageable, @Param("memberId") Long memberId);
}
