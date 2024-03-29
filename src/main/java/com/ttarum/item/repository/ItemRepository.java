package com.ttarum.item.repository;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemSummaryWithSimilarPrice;
import com.ttarum.item.dto.response.summary.ItemSummary;
import com.ttarum.item.dto.response.PopularItemSummaryInCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * {@link String query}를 이름으로 포함하는 제품을 {@link ItemSummary} 리스트로 반환합니다.
     *
     * @param query    검색어
     * @param pageable pageable
     * @return {@link ItemSummary} 리스트
     */
    @Query("""
            SELECT new com.ttarum.item.dto.response.summary.ItemSummary(i.id, i.category.name, i.name, i.price, AVG(r.star), i.itemImageUrl, false, i.createdAt, COUNT(oi.order.id))
            FROM Item i
            LEFT JOIN FETCH Review r
            ON r.item.id = i.id
            LEFT JOIN FETCH OrderItem oi
            ON oi.item.id = i.id
            WHERE i.name LIKE %:query%
            GROUP BY i.id, i.category.name, i.name, i.price, i.itemImageUrl, i.createdAt
            """)
    List<ItemSummary> getItemSummaryListByName(@Param("query") String query, Pageable pageable);

    /**
     * {@link String query}를 이름으로 포함하는 제품을 {@link ItemSummary} 리스트로 반환합니다.
     * {@link Long memberId}가 Id 값인 회원의 찜 목록에 포함 여부를 포함합니다.
     *
     * @param query    검색어
     * @param pageable pageable
     * @param memberId 회원의 Id 값
     * @return {@link ItemSummary} 리스트
     */
    @Query("""
            SELECT new com.ttarum.item.dto.response.summary.ItemSummary(i.id, i.category.name, i.name, i.price, AVG(r.star), i.itemImageUrl, (COUNT(wl.id) > 0), i.createdAt, COUNT(oi.order.id))
            FROM Item i
            LEFT JOIN FETCH Review r
            ON r.item.id = i.id
            LEFT JOIN FETCH OrderItem oi
            ON oi.item.id = i.id
            LEFT JOIN FETCH Wishlist wl
            ON wl.item.id = i.id AND wl.member.id = :memberId
            WHERE i.name LIKE %:query%
            GROUP BY i.id, i.category.name, i.name, i.price, i.itemImageUrl, i.createdAt
            """)
    List<ItemSummary> getItemSummaryListByName(@Param("query") String query, Pageable pageable, @Param("memberId") Long memberId);

    @Query("""
            SELECT new com.ttarum.item.dto.response.ItemSummaryWithSimilarPrice(i.id, i.name, i.price, i.itemImageUrl, false)
            FROM Item i
            WHERE i.price BETWEEN :lowPrice AND :highPrice
            """)
    List<ItemSummaryWithSimilarPrice> getItemSummaryWithSimilarPriceListByPriceRange(@Param("lowPrice") int lowPrice, @Param("highPrice") int highPrice, Pageable pageable);

    @Query("""
            SELECT new com.ttarum.item.dto.response.ItemSummaryWithSimilarPrice(i.id, i.name, i.price, i.itemImageUrl, COUNT(wl.id) > 0)
            FROM Item i
            LEFT JOIN FETCH Wishlist wl
            ON wl.item.id = i.id AND wl.member.id = :memberId
            WHERE i.price BETWEEN :lowPrice AND :highPrice
            GROUP BY i.id, i.name, i.price, i.itemImageUrl
            """)
    List<ItemSummaryWithSimilarPrice> getItemSummaryWithSimilarPriceListByPriceRange(@Param("lowPrice") int lowPrice, @Param("highPrice") int highPrice, @Param("memberId") long memberId, Pageable pageable);

    @Query("""
            SELECT new com.ttarum.item.dto.response.PopularItemSummaryInCategory(i.id, i.name, i.price, i.itemImageUrl, (COUNT(wl.id) > 0))
            FROM Item i
            LEFT JOIN FETCH Wishlist wl
            ON wl.item.id =  i.id AND wl.member.id = :memberId
            WHERE i.id in :ids
            """)
    List<PopularItemSummaryInCategory> getPopularItemSummaryListInCategory(@Param("ids") List<Long> itemIdList, @Param("memberId") long memberId);

    @Query("""
            SELECT new com.ttarum.item.dto.response.PopularItemSummaryInCategory(i.id, i.name, i.price, i.itemImageUrl, false)
            FROM Item i
            WHERE i.id in :ids
            """)
    List<PopularItemSummaryInCategory> getPopularItemSummaryListInCategory(@Param("ids") List<Long> itemIdList);
}
