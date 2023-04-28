package com.yakgwa.catchme.domain;

import com.yakgwa.catchme.exception.HasNotNicknameChangeCouponException;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String userId; // 일반 로그인 id
    private String password; // 일반 로그인 비밀번호
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
    public Member(String userId, String password, String nickname, String phoneNumber, String email, String birthYear, Gender gender) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthYear = birthYear;
        this.gender = gender;
        this.sumOfEvaluationScore = 0;
        this.numberOfEvaluation = 0;
        this.createdDateTime = LocalDateTime.now();
    }

    /**
     * 차단한(block) 멤버 blockMembers 관련 기능 필요
     * like 멤버 관리 필요
     */


    /**
     * 평균 점수
     */
    public double getEverageScore() {
        // 평가가 없으면 기본값 반환
        if (numberOfEvaluation == 0) {
            return 5;
        }
        return (double)sumOfEvaluationScore / numberOfEvaluation;
    }

    /**
     * 평가 받음
     */
    public void addScore(int score) {
        this.sumOfEvaluationScore += score;
        this.numberOfEvaluation++;
    }

    /**
     * 닉네임 변경
     */
    public void changeNickname(String nickname) {
        if (!isEditableNickname) {
            // Exception 만들기
            throw new HasNotNicknameChangeCouponException("닉네임 변경권이 없습니다.");
        }
        this.nickname = nickname;
        this.isEditableNickname = false;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }
}
