package com.yakgwa.catchme.dto;

import com.yakgwa.catchme.domain.Gender;
import com.yakgwa.catchme.domain.Mbti;
import com.yakgwa.catchme.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class SearchDetailedMemberInfo {
    private Long memberId;
    private String nickname;
    private List<String> imageUrls;
    private String birthYear;
    private String introduction;
    private Gender gender;
    private Mbti mbti;
    private double averageScore;
    private String email;

    public SearchDetailedMemberInfo(Member member, List<String> imageUrls) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.imageUrls = imageUrls;
        this.birthYear = member.getBirthYear();
        this.introduction = member.getIntroduction();
        this.gender = member.getGender();
        this.mbti = member.getMbti();
        this.averageScore = member.getAverageScore();
        this.email = member.getEmail();
    }

}
