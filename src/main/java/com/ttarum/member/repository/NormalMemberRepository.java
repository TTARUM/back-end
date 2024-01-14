package com.ttarum.member.repository;

import com.ttarum.member.domain.NormalMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalMemberRepository extends JpaRepository<NormalMember, Long> {
}
