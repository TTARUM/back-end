package com.ttarum.order.repository;

import com.ttarum.order.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.member.id = :memberId ORDER BY o.createdAt DESC")
    List<Order> findOrderListByMemberId(@Param("memberId") long memberId, Pageable pageable);

    @Query("""
            SELECT DISTINCT oi.item.id
            FROM Order o
            LEFT JOIN FETCH OrderItem oi
            ON oi.order.id = o.id
            WHERE (o.createdAt BETWEEN :before AND :after) AND oi.item.category.id = :categoryId
            """)
    List<Long> getPopularItemIdsByInstant(@Param("before") Instant before, @Param("after") Instant after, @Param("categoryId") Long categoryId, Pageable pageable);
}
