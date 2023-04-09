package com.yakgwa.catchme.dto;

import lombok.*;

@Getter
@Setter
public class LoginResponse {
    private String accessToken;


    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
