package com.ttarum.member.repository;

import com.ttarum.member.domain.WishList;
import com.ttarum.member.domain.WishListId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, WishListId> {

    @Query("SELECT wl FROM WishList wl WHERE wl.item.id = :itemId AND wl.member.id = :memberId")
    Optional<WishList> findByMemberIdAndItemId(@Param("memberId") long memberId, @Param("itemId") long itemId);
}
