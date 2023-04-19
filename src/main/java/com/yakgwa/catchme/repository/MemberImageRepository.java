package com.yakgwa.catchme.repository;

import com.yakgwa.catchme.domain.MemberImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {

    List<MemberImage> findByMemberId(Long memberId);
}
