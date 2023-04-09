package com.yakgwa.catchme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String accessToken; // 액세스토큰
    private String type;


    public LoginRequest() {} // 기본 생성자
    public LoginRequest(String accessToken) {
        this.accessToken = accessToken;
    }
}
