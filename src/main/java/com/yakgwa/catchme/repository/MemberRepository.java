package com.yakgwa.catchme.repository;

import com.yakgwa.catchme.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByPhoneNumber(String phoneNumber);
    Member findByNickname(String nickname);
}
