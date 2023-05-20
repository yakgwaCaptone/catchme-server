package com.yakgwa.catchme.dto;

import com.yakgwa.catchme.domain.Gender;
import com.yakgwa.catchme.domain.Mbti;
import com.yakgwa.catchme.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class SearchClassifiedMemberResponse {
    private Long memberId;
    private String nickname;
    private List<String> imageUrls;
    private Gender gender;
    private LocalDateTime classifiedDateTime;

    public SearchClassifiedMemberResponse(Member member, List<String> imageUrls, LocalDateTime classifiedDateTime) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.imageUrls = imageUrls;
        this.gender = member.getGender();
        this.classifiedDateTime = classifiedDateTime;
    }

}
