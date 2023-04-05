package com.yakgwa.catchme.repository;

import com.yakgwa.catchme.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
