package com.ttarum.item.repository;

import com.ttarum.item.domain.Item;
import com.ttarum.item.dto.response.ItemSummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
            SELECT new com.ttarum.item.dto.response.ItemSummaryResponse(i.id, i.category.name, i.name, i.price, AVG(r.star), i.itemImageUrl) 
            FROM Item i
            LEFT JOIN FETCH Review r 
            ON r.item.id = i.id
            WHERE i.name LIKE %:name%
            GROUP BY i.category.name
            """)
    List<ItemSummaryResponse> getItemSummaryListByName(@Param("name") String name);
}
