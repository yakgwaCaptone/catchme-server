package com.yakgwa.catchme.api;

import com.yakgwa.catchme.domain.Gender;
import com.yakgwa.catchme.domain.Likes;
import com.yakgwa.catchme.domain.Member;
import com.yakgwa.catchme.domain.MemberImage;
import com.yakgwa.catchme.dto.*;
import com.yakgwa.catchme.exception.DuplicateNicknameException;
import com.yakgwa.catchme.repository.MemberRepository;
import com.yakgwa.catchme.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;


    /**
     * 회원 가입
     */
    @PostMapping("/api/v1/join")
    public String signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        System.out.println("signUpRequestDto = " + signUpRequestDto);
        Member member = signUpRequestDto.createMember();
        memberService.join(member);
        return "";
    }

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



    /**
     * 프로필 이미지 업로드
     * AWS S3에 저장
     * Post 요청
     * multipart/form-data 형태
     *
     * 반환되는 imageId는 memberImageId
     * url로 사진 접근 가능
     */
    @PostMapping("/api/v1/members/{id}/images")
    public Result uploadProfileImages(
            Authentication authentication,
            @RequestParam("images") List<MultipartFile> imageFiles, @PathVariable("id") Long id) throws Exception {
        // 파라미터로 images 를 받음

        Long memberId = Long.parseLong(authentication.getName()); // jwt 인증 후 authentication에 멤버 id 저장됨
        log.info("/api/v1/members/{}/images 호출", memberId);
        List<MemberImage> memberImages = memberService.uploadProfileImage(memberId, imageFiles);

        // 프로필 이미지 업로드 결과 반환
        List<ImageResponseDto> collect = memberImages.stream()
                .map(memberImage -> new ImageResponseDto(memberImage.getId(), memberImage.getImage().getUrl()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    /**
     * 멤버 프로필 이미지 삭제
     * 표현은 images/imageId로 되어 있지만
     * 실질적인 값은 memberImageId
     */
    @DeleteMapping("/api/v1/members/{id}/images/{imageId}")
    public void deleteProfileImage(Authentication authentication,
                       @PathVariable("id") Long id,
                       @PathVariable("imageId") Long memberImageId) {

        Long memberId = Long.parseLong(authentication.getName()); // jwt 인증 후 authentication에 멤버 id 저장됨
        if (id != memberId) {
            // Todo api 요청시 자신의 id와 다르다는 Exception 만들기
            throw new RuntimeException("자신의 id만 접근 가능(delete)");
        }

        memberService.deleteProfileImage(memberId, memberImageId);
    }

    @GetMapping("/api/v1/members/{id}/images")
    public Result findProfileImages(@PathVariable("id") Long memberId) {
        List<ImageResponseDto> profileImages = memberService.findProfileImages(memberId);
        return new Result(profileImages.size(), profileImages);
    }

    /**
     * 점수 평가
     * targetId 평가 받는 대상자 id
     * Json 양식
     * { "score" : 숫자 }
     */
    @PostMapping("/api/v1/members/{targetId}/score")
    public void evaluate(Authentication authentication,
                         @PathVariable("targetId") Long targetId,
                         @RequestBody EvaluationRequest evaluationRequest) {
        evaluationRequest.getScore();
        log.info("score.intValue() = {}", evaluationRequest.getScore());
        Long memberId = Long.parseLong(authentication.getName());
        memberService.evaluate(memberId, targetId, evaluationRequest.getScore());
    }

    /**
     * 평균 점수 조회는
     * 사용자 정보 조회 만들 때 함께 포함하기
     */


    /**
     * 사용자 분류하기
     * like, dislike
     * 상대가 나를 like 했고
     * 나도 상대를 like 했다면
     * 1:1 매칭 결과 true,
     * else false
     */
    @PostMapping("/api/v1/classifications")
    public OneToOneMatching classifyLikeOrDislike(Authentication authentication,
                                      @RequestBody ClassificationRequest classificationRequest) {
        Long authMemberId = Long.parseLong(authentication.getName()); // jwt 인증 후 authentication에 멤버 id 저장됨

        // 인증 정보 다름 - api 게이트웨이로 분리되면 인증은 GW에서 처리
        if (authMemberId != classificationRequest.getMemberId()) {
            throw new RuntimeException("평가자와 로그인 유저 정보가 다릅니다.");
        }

        Long memberId = classificationRequest.getMemberId();
        Long targetId = classificationRequest.getTargetId();
        boolean status = classificationRequest.isStatus();

        // 분류
        return memberService.classifyLikeOrDislike(memberId, targetId, status);
    }

    /**
     * 사용자 분류 조회하기 (like, dislike)
     * /api/v1/classifications?memberId=3&status=true
     * /api/v1/classifications?targetId=3&status=true
     */
    @GetMapping("/api/v1/classifications")
    public Result getClassifications(Authentication authentication,
                                     @RequestParam(value = "memberId", required = false) Long memberId,
                                     @RequestParam(value = "targetId", required = false) Long targetId,
                                     @RequestParam("status") boolean status
                                     ) {
        List<Likes> likesList = new ArrayList<>();
        System.out.println("MemberController.getClassifications");
        // 보낸으로 사람 조회
        if (memberId != null) {
            likesList = memberService.findClassificationList(memberId, null, status);
        }
        // 받는 사람으로 조회
        else if (targetId != null) {
            likesList = memberService.findClassificationList(null, targetId, status);
        }

        // Dto로 변환하여 결과 반환
        List<ClassificationResponse> collect = likesList.stream()
                .map(likes -> new ClassificationResponse(likes.getMember().getId(), likes.getTarget().getId(), likes.isStatus(), likes.getCreatedDateTime()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }


    /**
     * 사용자 상세정보 조회
     */
    @GetMapping("/api/v1/members/{id}")
    public SearchDetailedMemberInfo getSearchDetailedMemberInfo(@PathVariable("id") Long memberId) {
        SearchDetailedMemberInfo detailedMemberInfo = memberService.findDetailedMemberInfo(memberId);
        return detailedMemberInfo;
    }

    /**
     * 분류할 사용자 조회
     * gender = 찾을 성별
     * /api/v1/search?gender=M
     * /api/v1/search?gender=W
     *
     * (옵션 : 조회할 개수 기본값 10, 최대값 50)
     * &count=5
     */
    @GetMapping("/api/v1/search")
    public Result getSearchTargetGender(Authentication authentication,
                                        @RequestParam("gender") String strGender,
                                        @RequestParam(value = "count", required = false) Long count) {

        Long memberId = Long.parseLong(authentication.getName()); // jwt 인증 후 authentication에 멤버 id 저장됨

        Gender gender;

        // 성별 확인
        if (strGender.equals("M")) {
            gender = Gender.M;
        } else if (strGender.equals("F")) {
            gender = Gender.W;
        } else {
            throw new RuntimeException("조회할 성별 값이 올바르지 않습니다.");
        }

        List<SearchDetailedMemberInfo> searchDetailedMemberInfos = memberService.findSearchTargetGender(memberId, gender, count);

        return new Result(searchDetailedMemberInfos.size(), searchDetailedMemberInfos);
    }
}
