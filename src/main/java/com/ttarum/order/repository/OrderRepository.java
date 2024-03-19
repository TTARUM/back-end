package com.ttarum.order.repository;

import com.ttarum.order.domain.Order;
import com.ttarum.order.dto.response.summary.OrderItemSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @SuppressWarnings("SqlResolve")
    @Query(value = """
            SELECT i.id AS itemId,
            i.item_image_url AS itemImageUrl,
            i.name AS itemName,
            i.price AS itemPrice,
            oi.amount AS amount,
            exists(
            SELECT 1 FROM Review r WHERE r.order_id = oi.order_id AND r.item_id = oi.item_id AND r.is_deleted = false
            ) AS hasReview
            FROM `Order` o
            LEFT JOIN Order_Item oi
            ON o.id = oi.order_id
            LEFT JOIN Item i
            ON i.id = oi.item_id
            WHERE o.id = :orderId
            limit :limit
            """, nativeQuery = true)
    List<OrderItemSummary> findOrderItemListByOrderId(@Param("orderId") long orderId, @Param("limit") int limit);

    @SuppressWarnings("SqlResolve")

    @Query(value = """
            SELECT i.id AS itemId,
            i.item_image_url AS itemImageUrl,
            i.name AS itemName,
            i.price AS itemPrice,
            oi.amount AS amount,
            exists(
            SELECT 1 FROM Review r WHERE r.order_id = oi.order_id AND r.item_id = oi.item_id AND r.is_deleted = false
            ) AS hasReview
            FROM `Order` o
            LEFT JOIN Order_Item oi
            ON o.id = oi.order_id
            LEFT JOIN Item i
            ON i.id = oi.item_id
            WHERE o.id = :orderId
            """, nativeQuery = true)
    List<OrderItemSummary> findOrderItemListByOrderId(@Param("orderId") long orderId);

    @Query("SELECT o FROM Order o WHERE o.member.id = :memberId ORDER BY o.createdAt DESC")
    List<Order> findOrderListByMemberId(@Param("memberId") long memberId, Pageable pageable);

    @Query("""
            SELECT DISTINCT oi.item.id
            FROM Order o
            LEFT JOIN FETCH OrderItem oi
            ON oi.order.id = o.id
            WHERE (o.createdAt BETWEEN :before AND :after) AND oi.item.category.name = :categoryName
            """)
    List<Long> getPopularItemIdsByInstant(@Param("before") Instant before, @Param("after") Instant after, @Param("categoryName") String categoryName, Pageable pageable);
}
