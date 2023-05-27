package com.yakgwa.catchme.api;

import com.yakgwa.catchme.domain.Member;
import com.yakgwa.catchme.dto.LoginRequest;
import com.yakgwa.catchme.dto.OAuthLoginRequest;
import com.yakgwa.catchme.dto.LoginResponse;
import com.yakgwa.catchme.dto.SignUpRequestDto;
import com.yakgwa.catchme.service.AuthService;
import com.yakgwa.catchme.service.MemberService;
import com.yakgwa.catchme.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final AuthService authService;
    @Value("${jwt.secret}")
    private String secret;
    private Long expiredMs = 1000 * 60 * 43200L; // 한 시간 (1000 * 60 == 1분) 1달 설정

    /**
     * Oauth 로그인 기능 (자동 회원 가입)
     * @param OAuthLoginRequest DTO 를 통해
     * accessToken, type 을 입력 받는다.
     * type - naver, google 등 OAuth 종류
     * accessToken - 각 type에 맞는 토큰
     *
     * @return loginResponse(JWT)
     * 생성한 accessToken(jwt)를 반환
     */
    @PostMapping("/api/v1/oauth/login")
    public LoginResponse oAuthLogin(@RequestBody OAuthLoginRequest OAuthLoginRequest) {
        // api 통신으로 데이터 불러옴
        SignUpRequestDto signUpRequestDto = authService.loadData(OAuthLoginRequest);
        Member findMember = memberService.findByUserId(signUpRequestDto.getUserId());

        // 회원 가입
        if (findMember == null) {
            System.out.println("회원 가입 필요");
            Long memberId = memberService.join(signUpRequestDto.createMember()); // 회원가입
            String jwt = JwtUtil.createJwt(memberId, secret, expiredMs);
            return new LoginResponse(jwt, memberId);
        }

        // 가입된 회원일 경우
        String jwt = JwtUtil.createJwt(findMember.getId(), secret, expiredMs);
        return new LoginResponse(jwt, findMember.getId());
    }


    /**
     * 일반 로그인 기능
     * @param LoginRquest DTO 를 통해
     * userId, password를 입력 받는다.
     *
     * @return loginResponse(JWT)
     * 생성한 accessToken(jwt)를 반환
     */
    @PostMapping("/api/v1/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return memberService.login(loginRequest, secret, expiredMs);
    }


}
