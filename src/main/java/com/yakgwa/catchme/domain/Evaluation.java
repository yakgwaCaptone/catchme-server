package com.yakgwa.catchme.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


// 평가
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation {
    @Id @GeneratedValue
    @Column(name = "evaluation_id")
    private Long id;

    /**
     * 단방향 매핑
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Member target;

    private int score;

    LocalDateTime createdDateTime;

    public Evaluation (Member member, Member target, int score) {
        if (score < 0 || score > 5) {
            throw new RuntimeException("평가 가능한 점수 범위를 벗어났습니다.");
        }
        this.member = member;
        this.target = target;
        target.addScore(score); // 평가 점수 적용
        this.score = score;
        this.createdDateTime = LocalDateTime.now();
    }
}
