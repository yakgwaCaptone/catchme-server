package com.yakgwa.catchme.config;

import com.yakgwa.catchme.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration      // 설정 어노테이션
@EnableWebSecurity  // 시큐리티 활성화 - 기본 스프링 필터체인에 등록
@RequiredArgsConstructor
public class AuthenticationConfig {
    private final MemberService memberService;
    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // 인증을 토큰 기반으로 하기 때문에 ui 기능 disable
                .httpBasic().disable()
                // 사이트 간 요청 위조 (Cross-site request forgery) 보안 끄기
                // api는 stateless 환경이기에 해당 기능이 없어도 되는 것 같다.
                .csrf().disable()
                // CORS (Cross-Origin Resource Sharing)
                // CORS 란 서로 다른 Origin끼리 요청을 주고받을 수 있게 정해둔 표준
                .cors().and()
                // http 요청시 인증과 관련된 설정
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/oauth/login").permitAll()   // 해당 api 요청 허용
                .requestMatchers("/api/v1/login").permitAll()   // 해당 api 요청 허용
                .requestMatchers("/api/v1/join").permitAll()   // 해당 api 요청 허용
                .requestMatchers(HttpMethod.GET, "/api/v1/members/*/images").permitAll()// 프로필 사진 조회 허용
                .requestMatchers(HttpMethod.POST, "/api/**").authenticated()  // 해당 api 요청은 인증 필요
                .anyRequest().authenticated()   // 위 설정을 제외한 나머지 전부 인증 필요
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt 사용할 경우 - 세션 끄기
                .and()
                .addFilterBefore(new JwtFilter(memberService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
