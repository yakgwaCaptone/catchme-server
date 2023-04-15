package com.yakgwa.catchme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberUpdateResponseDto {
    String nickname;
    String introduction;
}
