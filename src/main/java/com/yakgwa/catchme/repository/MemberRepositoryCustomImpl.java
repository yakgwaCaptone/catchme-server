package com.yakgwa.catchme.repository;

import com.yakgwa.catchme.domain.Gender;
import com.yakgwa.catchme.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final EntityManager em;


    /**
     * 평가한 대상들 불러옴
     * 대상에 포함되지 않고, 특정 성별을 만족하는 멤버 조회
     * count 개수 만큼
     */
    @Override
    public List<Member> findSearchTargetPage(Long memberId, Gender gender, int count) {
        if (count <= 1) {
            count = 1;
        }
        if (count > 50) {
            count = 50;
        }
        return em.createQuery("select m from Member m where " +
                "m.id not in (select l.target.id from Likes l where l.member.id = :memberId) and m.gender = :gender", Member.class)
                .setParameter("memberId", memberId)
                .setParameter("gender", gender)
                .setFirstResult(0)
                .setMaxResults(count)
                .getResultList();
    }
}
