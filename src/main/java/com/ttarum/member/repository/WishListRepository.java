package com.ttarum.member.repository;

import com.ttarum.member.domain.WishList;
import com.ttarum.member.domain.WishListId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, WishListId> {

    Optional<WishList> findByMemberIdAndItemId(long memberId, long itemId);
}
