package com.yakgwa.catchme.dto;

import com.yakgwa.catchme.domain.Mbti;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberUpdateResponseDto {
    String nickname;
    String introduction;
    Mbti mbti;
}
