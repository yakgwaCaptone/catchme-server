package com.yakgwa.catchme.repository;

import com.yakgwa.catchme.domain.MemberImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {

    // fetch join을 통해 MemberImage 조회시 Image 같이 조회 == 쿼리 하나
    @Query("select mi from MemberImage mi join fetch mi.image where mi.member.id = :memberId")
    List<MemberImage> findByMemberId(@Param("memberId") Long memberId);
}
