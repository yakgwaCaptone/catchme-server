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
        String createdJwt = JwtUtil.createJwt("1234", secret, expiredMs);
        // when
        // then
        Assertions.assertThat(StringUtils.hasText(createdJwt)).isTrue();
    }

    @Test
    @DisplayName("JWT 만료시간 테스트")
    public void isExpired() throws Exception {
        // given
        String expiredJwt = JwtUtil.createJwt("1234", secret, 3000L); // 3초 만료시간
        // when
        Assertions.assertThat(JwtUtil.isExpired(expiredJwt, secret)).isFalse(); // 만료
        Thread.sleep(3000L);

        // 만료 Exception 던져짐
        org.junit.jupiter.api.Assertions.assertThrows(
                io.jsonwebtoken.ExpiredJwtException.class,
                () -> JwtUtil.isExpired(expiredJwt, secret) );


        // then
    }
}