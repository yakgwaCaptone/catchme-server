package com.yakgwa.catchme.dto;

import lombok.*;

@Getter
@Setter
public class LoginResponse {
    private String accessToken;
    private Long id;

    public LoginResponse(String accessToken, Long id) {
        this.accessToken = accessToken;
        this.id = id;
    }
}
