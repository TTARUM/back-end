package com.ttarum.member.repository;

import com.ttarum.member.domain.NormalMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NormalMemberRepository extends JpaRepository<NormalMember, Long> {
    Optional<NormalMember> findNormalMemberByLoginId(String loginId);

    boolean existsByEmail(String email);
}
