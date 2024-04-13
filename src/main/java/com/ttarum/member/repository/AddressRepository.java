package com.ttarum.member.repository;

import com.ttarum.member.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByMemberId(Long userId);
}
