package com.yakgwa.catchme.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthLoginRequest {

    private String accessToken; // 액세스토큰
    private String type;


    public OAuthLoginRequest() {} // 기본 생성자
    public OAuthLoginRequest(String accessToken) {
        this.accessToken = accessToken;
    }
}
