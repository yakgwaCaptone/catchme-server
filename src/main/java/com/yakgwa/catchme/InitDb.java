package com.yakgwa.catchme;

import com.yakgwa.catchme.domain.Member;
import com.yakgwa.catchme.dto.LoginResponse;
import com.yakgwa.catchme.dto.MemberDto;
import com.yakgwa.catchme.service.MemberService;
import com.yakgwa.catchme.utils.JwtUtil;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 테스트용 초기 DB 넣기
 *
 * 나중에 삭제 예정
 */

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        System.out.println("initService = " + initService);
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final MemberService memberService;

        @Value("${jwt.secret}")
        private String secret;
        private Long expiredMs = 1000 * 60 * 60L; // 한 시간

        /**
         * 우회적으로 강제 가입
         */
        public void dbInit() {
            MemberDto memberDto;
            List<Map<String, String>> infoList = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                String gender = i % 2 == 0 ? "M" : "G";
                String birthYear = Integer.toString(1995 + i);
                String email = "hello" + i + "@mail.com";
                String phoneNumber = "010-0000-" + (1000 + i);
                String nickname = "hello" + i;
                memberDto = new MemberDto(nickname, phoneNumber, email, birthYear, gender);

                Long memberId = memberService.join(memberDto.createMember()); // 회원가입

                //Long memberId = member.getId();
                String jwt = JwtUtil.createJwt(memberId, secret, expiredMs);

                Map<String, String> map = new HashMap<>();
                map.put("memberId", ""+memberId);
                map.put("nickname", nickname);
                map.put("accessToken", jwt);
                infoList.add(map);
            }


            System.out.println("임시 DB 데이터 - 회원가입");
            for (int i = 0; i < 10; i++) {
                System.out.println(infoList.get(i).toString());
            }

        }
    }
}
