package com.yakgwa.catchme.repository;

import com.yakgwa.catchme.domain.Gender;
import com.yakgwa.catchme.domain.Member;
import org.springframework.data.repository.query.Param;

import java.util.List;

// 커스텀 레포지토리 함수 정의만
public interface MemberRepositoryCustom {

    List<Member> findSearchTargetPage(Long memberId, Gender gender, int count);
}
