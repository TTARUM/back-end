package com.ttarum.member.repository;

import com.ttarum.member.domain.WishList;
import com.ttarum.member.domain.WishListId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, WishListId> {


    @Query("SELECT wl FROM WishList wl WHERE wl.member.id = :memberId")
    List<WishList> findByMemberId(@Param("memberId") long memberId, Pageable pageable);
}
