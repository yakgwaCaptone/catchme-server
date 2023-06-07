package com.yakgwa.catchme.dto;

import com.yakgwa.catchme.domain.Gender;
import lombok.*;

@Getter
@Setter
public class LoginResponse {
    private String accessToken;
    private Long id;
    private Gender gender;

    public LoginResponse(String accessToken, Long id, Gender gender) {
        this.accessToken = accessToken;
        this.id = id;
        this.gender = gender;
    }
}
