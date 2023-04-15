package com.yakgwa.catchme.api;

import com.yakgwa.catchme.domain.Member;
import com.yakgwa.catchme.dto.LoginRequest;
import com.yakgwa.catchme.dto.LoginResponse;
import com.yakgwa.catchme.dto.MemberDto;
import com.yakgwa.catchme.exception.MemberDataNotLoadException;
import com.yakgwa.catchme.service.AuthService;
import com.yakgwa.catchme.service.MemberService;
import com.yakgwa.catchme.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final AuthService authService;
    @Value("${jwt.secret}")
    private String secret;
    private Long expiredMs = 1000 * 60 * 60L; // 한 시간

    /**
     * 로그인 기능 (자동 회원 가입)
     * @param loginRequest DTO 를 통해
     * accessToken, type 을 입력 받는다.
     * type - naver, google 등 OAuth 종류
     * accessToken - 각 type에 맞는 토큰
     *
     * @return loginResponse(JWT)
     * 생성한 accessToken(jwt)를 반환
     */
    @PostMapping("/api/v1/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        // api 통신으로 데이터 불러옴
        MemberDto memberDto = authService.loadData(loginRequest);
        Member findMember = memberService.findByPhoneNumber(memberDto.getPhoneNumber());

        // 회원 가입
        if (findMember == null) {
            System.out.println("회원 가입 필요");
            Long memberId = memberService.join(memberDto.createMember()); // 회원가입
            String jwt = JwtUtil.createJwt(memberId, secret, expiredMs);
            return new LoginResponse(jwt, memberId);
        }

        // 가입된 회원일 경우
        String jwt = JwtUtil.createJwt(findMember.getId(), secret, expiredMs);
        return new LoginResponse(jwt, findMember.getId());
    }



}
