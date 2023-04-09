package com.yakgwa.catchme.api;

import com.yakgwa.catchme.domain.Member;
import com.yakgwa.catchme.service.MemberService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    /**
     * 닉네임 변경
     * json "nickname":{변경할 닉네임}
     * 현재 1번밖에 못 바꾸도록 제한 걸려있음
     */
    @PostMapping("/api/v1/members/nickname")
    public String changeNickname(Authentication authentication, @RequestBody Map<String, String> request) {
        String nickname = request.get("nickname");

        System.out.println("authentication.getName() = " + authentication.getName());
        Long memberId = Long.parseLong(authentication.getName()); // 인증 정보로부터 id 추출 - name 자리에 id 값이 String으로 들어가 있음
        memberService.changeNickname(memberId, nickname);

        return "변경 완료";
    }
}
