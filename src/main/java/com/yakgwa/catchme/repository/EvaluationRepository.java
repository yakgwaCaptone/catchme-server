package com.yakgwa.catchme.repository;

import com.yakgwa.catchme.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Evaluation findByMemberIdAndTargetId(Long memberId, Long TargetId);
}
