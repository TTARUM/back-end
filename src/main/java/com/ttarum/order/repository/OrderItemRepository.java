package com.ttarum.order.repository;

import com.ttarum.order.domain.OrderItem;
import com.ttarum.order.dto.response.OrderItemSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("""
            SELECT new com.ttarum.order.dto.response.OrderItemSummary(i.id, i.itemImageUrl, i.name, i.price, oi.amount)
            FROM OrderItem oi JOIN oi.item i
            WHERE oi.order.id = :orderId
            """)
    List<OrderItemSummary> findOrderItemListByOrderId(@Param("orderId") long orderId);
}
