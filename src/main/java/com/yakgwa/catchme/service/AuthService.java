package com.yakgwa.catchme.service;

import com.yakgwa.catchme.dto.LoginRequest;
import com.yakgwa.catchme.dto.LoginResponse;
import com.yakgwa.catchme.dto.MemberDto;
import com.yakgwa.catchme.utils.OauthResourceApi;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public MemberDto loadData(LoginRequest loginRequest) {
        String type = loginRequest.getType();
        String accessToken = loginRequest.getAccessToken();
        MemberDto memberDto = null;

        switch (type) {
            case "naver":
                memberDto = OauthResourceApi.loadDataFromNaver(accessToken);
                break;
            case "google":
                break;
            case "test": // 코드 흐름이 좋지는 않지만 일단 이렇게 구성
                memberDto = OauthResourceApi.loadDataFromTest(accessToken);
                break;
            default:
                throw new RuntimeException("잘못된 요청입니다.");
        }
        return memberDto;
    }

}
