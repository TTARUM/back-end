package com.ttarum.member.repository;

import com.ttarum.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalMemberRepository extends JpaRepository<Member, Long> {
}
