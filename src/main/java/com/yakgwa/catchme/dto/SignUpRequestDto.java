package com.yakgwa.catchme.dto;

import com.yakgwa.catchme.domain.Gender;
import com.yakgwa.catchme.domain.Member;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    private String userId;
    private String password;
    private String phoneNumber; // 휴대전화
    private String email; // 이메일
    private String nickname;
    private String birthYear;
    private String gender;

    public Member createMember() {
        if (gender.equals("M")) {
            return new Member(userId, password, nickname, phoneNumber, email, birthYear, Gender.M);
        }
        else if (gender.equals("W")) {
            return new Member(userId, password, nickname, phoneNumber, email, birthYear, Gender.W);
        }
        else {
            throw new RuntimeException("성별 오류");
        }
    }
}
