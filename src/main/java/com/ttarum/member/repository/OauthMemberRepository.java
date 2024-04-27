package com.ttarum.member.repository;

import com.ttarum.member.domain.OauthMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthMemberRepository extends JpaRepository<OauthMember, Long> {

    boolean existsByEmail(String email);
}
