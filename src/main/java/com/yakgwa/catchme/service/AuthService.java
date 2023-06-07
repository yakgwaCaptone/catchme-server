package com.yakgwa.catchme.service;

import com.yakgwa.catchme.dto.OAuthLoginRequest;
import com.yakgwa.catchme.dto.SignUpRequestDto;
import com.yakgwa.catchme.utils.OauthResourceApi;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public SignUpRequestDto loadData(OAuthLoginRequest oAuthLoginRequest) {
        String type = oAuthLoginRequest.getType();
        String accessToken = oAuthLoginRequest.getAccessToken();
        SignUpRequestDto signUpRequestDto = null;

        switch (type) {
            case "naver":
                signUpRequestDto = OauthResourceApi.loadDataFromNaver(accessToken);
                break;
            case "google":
                break;
            case "test": // 코드 흐름이 좋지는 않지만 일단 이렇게 구성
                signUpRequestDto = OauthResourceApi.loadDataFromTest(accessToken);
                break;
            default:
                throw new RuntimeException("잘못된 요청입니다.");
        }
        return signUpRequestDto;
    }

}
