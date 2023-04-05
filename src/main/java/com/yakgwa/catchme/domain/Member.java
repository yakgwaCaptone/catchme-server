package com.yakgwa.catchme.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String nickname;
    private String phoneNumber;
    private String email;
    private boolean isEditableNickname = true;
    private String birthYear;
    private String introduction;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    private int sumOfEvaluationScore;
    private int numberOfEvaluation;

    private LocalDateTime createdDateTime;

    @OneToMany(mappedBy = "member")
    private List<MemberSns> memberSnses = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberImage> memberImages = new ArrayList<>();

    @OneToMany(mappedBy = "target")
    private List<BlockMember> blockMembers = new ArrayList<>();

    @OneToMany(mappedBy = "target")
    private List<Likes> likes= new ArrayList<>();


    protected Member() {}
    public Member(String nickname, String phoneNumber, String email, String birthYear, Gender gender) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthYear = birthYear;
        this.gender = gender;
        this.sumOfEvaluationScore = 5;
        this.numberOfEvaluation = 1;
        this.createdDateTime = LocalDateTime.now();
    }

    /**
     * 차단한(block) 멤버 blockMembers 관련 기능 필요
     * like 멤버 관리 필요
     */

    /**
     *   프로필 이미지 연계 필요
     *   사진 추가, 삭제 기능 필요
     *
     */




    /**
     * 평균 점수
     */
    double getEverageScore() {
        return sumOfEvaluationScore / numberOfEvaluation;
    }

    /**
     * 닉네임 변경
     */
    public void changeNickname(String nickname) {
        if (!isEditableNickname) {
            // Exception 만들기
            throw new IllegalStateException("닉네임 변경권이 없습니다.");
        }
        this.nickname = nickname;
        this.isEditableNickname = false;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
