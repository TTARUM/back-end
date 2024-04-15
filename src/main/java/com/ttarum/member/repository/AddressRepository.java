package com.ttarum.member.repository;

import com.ttarum.member.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByMemberId(Long userId);

    @Query("SELECT a FROM Address a WHERE a.member.id = :memberId AND a.isDefault = true")
    Optional<Address> findDefaultAddressByMemberId(Long memberId);
}
