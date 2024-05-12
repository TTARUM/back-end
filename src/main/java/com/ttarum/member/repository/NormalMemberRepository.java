package com.ttarum.member.repository;

import com.ttarum.member.domain.NormalMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NormalMemberRepository extends JpaRepository<NormalMember, Long> {
    Optional<NormalMember> findNormalMemberByLoginId(String loginId);

    boolean existsByEmail(String email);

    @Query("SELECT nm FROM NormalMember nm WHERE nm.email = :email")
    Optional<NormalMember> findNormalMemberByEmail(@Param("email") String email);
}
