package com.yakgwa.catchme.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String userId;
    private String password;


    public LoginRequest() {} // 기본 생성자
}
