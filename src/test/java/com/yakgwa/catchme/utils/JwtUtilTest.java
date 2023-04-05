package com.yakgwa.catchme.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Value("${jwt.secret}") // 설정 파일로부터 읽어옴 - jwt.yml 파일에 작성됨
    private String secret;
    private Long expiredMs = 1000 * 2L; // 2초 (1000 * 60 = 1분)
    @Test
    @DisplayName("JWT 생성")
    public void createJwt() throws Exception {
        // given
        String createdJwt = JwtUtil.createJwt(1234L, secret, expiredMs);
        // when
        // then
        Assertions.assertThat(StringUtils.hasText(createdJwt)).isTrue();
    }

    @Test
    @DisplayName("JWT 만료시간 테스트")
    public void isExpired() throws Exception {
        // given
        String expiredJwt = JwtUtil.createJwt(1234L, secret, 3000L); // 3초 만료시간
        // when
        Assertions.assertThat(JwtUtil.isExpired(expiredJwt, secret)).isFalse(); // 만료 안됨
        Thread.sleep(3000L);

        // then
        // 만료 Exception 던져짐
        org.junit.jupiter.api.Assertions.assertThrows(
                io.jsonwebtoken.ExpiredJwtException.class,
                () -> JwtUtil.isExpired(expiredJwt, secret) );

    }

    @Test
    @DisplayName("JWT 멤버ID 추출 검사")
    public void getMemberId() throws Exception {
        // given
        Long memberId = 1234L;
        String createdJwt = JwtUtil.createJwt(memberId, secret, expiredMs);     // 자신 토큰
        String otherJwt = JwtUtil.createJwt(1111L, secret, expiredMs);// 다른 토큰

        // when
        Long findMemberId = JwtUtil.getMemberId(createdJwt, secret);
        Long findOtherMemberId = JwtUtil.getMemberId(otherJwt, secret);

        // then
        Assertions.assertThat(memberId).isEqualTo(findMemberId);
        Assertions.assertThat(memberId).isNotEqualTo(findOtherMemberId);

    }

    @Test
    @DisplayName("Jwt 검증")
    public void isValid() {
        String otherSecretKey = "";
        for (int i = 0; i < 26; i++) { // 시크릿 키는 256bit 이상
            otherSecretKey += "abcdefghij";
        }

        Long memberId = 1234L;
        String generatedFromDifferentKeyJwt = JwtUtil.createJwt(memberId, otherSecretKey, expiredMs);   // 다른 시크릿 키로 생성

        Assertions.assertThat(JwtUtil.isValid(generatedFromDifferentKeyJwt, secret)).isFalse();
    }
}