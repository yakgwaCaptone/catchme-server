package com.yakgwa.catchme.repository;

import com.yakgwa.catchme.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Likes findByMemberIdAndTargetId(Long memberId, Long targetId);
    Likes findByMemberIdAndTargetIdAndStatus(Long memberId, Long targetId, boolean status);

    // 평가자, 상태로 조회 - 정렬 : 시간 내림차순
    @Query("select l from Likes l where l.member.id = :memberId and l.status = :status order by l.createdDateTime desc")
    List<Likes> findByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") boolean status);
    // 평가 대상, 상태로 조회 - 정렬 : 시간 내림차순
    @Query("select l from Likes l where l.target.id = :targetId and l.status = :status order by l.createdDateTime desc")
    List<Likes> findByTargetIdAndStatus(@Param("targetId") Long targetId, @Param("status") boolean status);
}
