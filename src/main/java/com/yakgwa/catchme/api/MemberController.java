package com.yakgwa.catchme.api;

import com.yakgwa.catchme.domain.Member;
import com.yakgwa.catchme.dto.MemberUpdateRequestDto;
import com.yakgwa.catchme.dto.MemberUpdateResponseDto;
import com.yakgwa.catchme.exception.DuplicateNicknameException;
import com.yakgwa.catchme.repository.MemberRepository;
import com.yakgwa.catchme.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    /**
     * 사용자 정보 업데이트
     * json {
     *  "nickname":{변경할 닉네임}
     *  "introduction" : {자기 소개}
     * }
     * 현재 닉네임은 1번밖에 못 바꾸도록 제한 걸려있음
     */
    @PostMapping("/api/v1/members/{id}")
    public MemberUpdateResponseDto memberInfoUpdate(Authentication authentication, @PathVariable("id") Long memberId, @RequestBody MemberUpdateRequestDto memberUpdateRequestDto) {
        // 인증 정보로부터 id 추출 - name 자리에 id 값이 String으로 들어가 있음
        // 다른 사람 정보를 변경시도
        if (memberId!= Long.parseLong(authentication.getName())) {
            throw new RuntimeException("다른 사람 정보는 변경할 수 없습니다.");
        }

        System.out.println("authentication.getName() = " + authentication.getName());
        return memberService.updateInformation(memberId, memberUpdateRequestDto);
    }

    /**
     * 사용 가능한 닉네임 조회
     * @return
     * 해당 닉네임이 없음 = 정상 (상태 200)
     * 해당 닉네임 존재  = 예외 발생 (상태 400 번대)
     */
    @GetMapping("/api/v1/nicknames/{nickname}")
    public String usableNickname(@PathVariable("nickname") String nickname) {
        log.info("nickname : {}", nickname);
        Member findMember = memberRepository.findByNickname(nickname);
        if (findMember == null) {
            return nickname + " 사용 가능";
        }
        throw new DuplicateNicknameException(nickname + " 해당 닉네임은 사용할 수 없음");
    }
}
