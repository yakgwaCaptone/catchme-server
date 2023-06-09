package com.yakgwa.catchme.dto;

import com.yakgwa.catchme.domain.Gender;
import com.yakgwa.catchme.domain.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {
    private String userId;
    private String password;
    private String phoneNumber; // 휴대전화
    private String email; // 이메일
    private String birthYear; // 생년
    private String gender; // 성별
    private String nickname; // 닉네임 초기값 = 불러온 아이디


    public MemberDto(String userId, String password, String nickname, String phoneNumber, String email, String birthYear, String gender)  {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthYear = birthYear;
        this.gender = gender;
    }

    public Member createMember() {
        Gender genderType = gender.equals("M") ? Gender.M : Gender.W;
        return new Member(userId, password, nickname, phoneNumber, email, birthYear, genderType);
    }
}
