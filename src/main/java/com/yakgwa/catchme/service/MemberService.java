package com.yakgwa.catchme.service;

import com.yakgwa.catchme.domain.*;
import com.yakgwa.catchme.dto.*;
import com.yakgwa.catchme.exception.DuplicateNicknameException;
import com.yakgwa.catchme.repository.*;
import com.yakgwa.catchme.utils.FileHandler;
import com.yakgwa.catchme.utils.JwtUtil;
import com.yakgwa.catchme.utils.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberImageRepository memberImageRepository;
    private final FileHandler fileHandler;
    private final EvaluationRepository evaluationRepository;
    private final LikesRepository likesRepository;
    private final S3Util s3Util;
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);    // 중복 회원 검사
        isDuplicationNickname(member.getNickname()); // 중복 닉네임 검사
        member.encodePassword(passwordEncoder); // 암호화
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 일반 회원 로그인
     */
    @Transactional
    public LoginResponse login(LoginRequest loginRequest, String secret, Long expiredMs) {
        String userId = loginRequest.getUserId();

        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new RuntimeException("존재하지 않는 userId 입니다");
        }

        // 비밀번호 인코딩 후 단순 비교시 맞지 않음
        // encoder의 matches 를 통해 비교
        boolean isEqualPassword = passwordEncoder.matches(loginRequest.getPassword(), member.getPassword());
        if (isEqualPassword == false)  {
            throw new RuntimeException("해당 userId의 비밀번호가 맞지 않습니다");
        }

        String jwt = JwtUtil.createJwt(member.getId(), secret, expiredMs);
        return new LoginResponse(jwt, member.getId());
    }

    /**
     * 중복 회원 검사 (기준 : 사용자 로그인 id)
     */
    private boolean validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByUserId(member.getUserId());
        if (findMember == null) {
            return false;
        }

        throw new IllegalStateException("이미 존재하는 회원입니다.");
    }

    /**
     * 중복 닉네임일 경우 예외 발생
     */
    public void isDuplicationNickname(String nickname) {
        if(memberRepository.findByNickname(nickname) != null) {
            throw new DuplicateNicknameException("이미 존재하는 닉네임입니다.");
        }
    }

    @Transactional
    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).get();
    }



    @Transactional
    public void changeNickname(Long memberId, String nickname) {
        isDuplicationNickname(nickname);
        memberRepository.findById(memberId).get()
                .changeNickname(nickname);;
    }

    @Transactional
    public void updateIntroduction(Long memberId, String introduction) {
        Member member = memberRepository.findById(memberId).get();
        member.setIntroduction(introduction);
    }

    /**
     * 사용자 정보 업데이트 - 닉네임, 자기소개
     * exception 발생시 롤백
     * @param memberId
     * @param memberUpdateRequestDto
     */
    @Transactional
    public MemberUpdateResponseDto updateInformation(Long memberId, MemberUpdateRequestDto memberUpdateRequestDto) {
        String introduction = memberUpdateRequestDto.getIntroduction();
        String nickname = memberUpdateRequestDto.getNickname();
        Mbti mbti = memberUpdateRequestDto.getMbti();


        Member member = memberRepository.findById(memberId).get();

        // 변경 사항에 닉네임이 있을 때
        if (StringUtils.hasText(nickname)) {
            isDuplicationNickname(nickname);
            member.changeNickname(nickname);
        }

        member.setIntroduction(introduction);
        member.setMbti(mbti);
        return new MemberUpdateResponseDto(member.getNickname(), member.getIntroduction(), member.getMbti());
    }


    public Member findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }

    public Member findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }


    /**
     * 이미지 업로드 MultipartFile로 받기
     */
    @Transactional
    public List<MemberImage> uploadProfileImage(Long memberId, List<MultipartFile> imageFiles) throws IOException {

        List<String> urls = s3Util.upload(imageFiles);
        List<Image> images = urls.stream()
                .map(url -> new Image(url, memberId.toString()))
                .collect(Collectors.toList());

        if (images.isEmpty()) {
            // Todo 파일 없을 때 throw 예외 및 예외처리
            return null;
        }

        Member member = memberRepository.findById(memberId).get();

        List<MemberImage> memberImages = new ArrayList<>();
        MemberImage createdMemberImage;
        // 이미지, 프로필 사진 저장
        for (int i = 0; i < images.size(); i++) {
            //images.set(i, imageRepository.save(images.get(i)));
            createdMemberImage = new MemberImage(member, images.get(i));
            memberImageRepository.save(createdMemberImage);
            memberImages.add(createdMemberImage);
        }

        // 반환
        return memberImages;
    }

    /**
     * 단건 삭제
     */
    @Transactional
    public void deleteProfileImage(Long memberId, Long memberImageId) {
        MemberImage memberImage = memberImageRepository.findById(memberImageId).get();

        if (memberId != memberImage.getMember().getId()) {
            // TODO 사용자 정의 exception 만들기
            throw new RuntimeException("자신의 사진만 삭제 가능합니다");
        }

        memberImageRepository.delete(memberImage);
    }

    /**
     * 이미지 아이디, 이미지 url
     */
    public List<ImageResponseDto> findProfileImages(Long memberId) {
        return memberImageRepository.findByMemberId(memberId)
                .stream()
                .map(memberImage -> new ImageResponseDto(memberImage.getId(), memberImage.getImage().getUrl()))
                .collect(Collectors.toList());
    }

    /**
     *  url 만 String List로 받기
     */
    public List<String> findProfileImagesUrls(Long memberId) {
        return memberImageRepository.findByMemberId(memberId)
                .stream()
                .map(memberImage -> new String(memberImage.getImage().getUrl()))
                .collect(Collectors.toList());

    }


    /**
     * 평가
     * 평가자, 평가 대상, 점수
     */
    @Transactional
    public void evaluate(Long memberId, Long targetId, int score) {
        // 기존 데이터 확인
        // memberId, targetId 로 조회해서 있으면 throw exception

        if (evaluationRepository.findByMemberIdAndTargetId(memberId, targetId) != null) {
            throw new RuntimeException("이미 평가한 사람입니다.");
        }

        Member member = memberRepository.findById(memberId).get();
        Member target = memberRepository.findById(targetId).get();
        Evaluation evaluation = new Evaluation(member, target, score);

        evaluationRepository.save(evaluation);
    }

    /**
     * 분류하다
     * like, dislike
     */
    @Transactional
    public OneToOneMatching classifyLikeOrDislike(Long memberId, Long targetId, boolean likeStatus) {
        Likes findLikes = likesRepository.findByMemberIdAndTargetId(memberId, targetId);

        if (findLikes != null) {
            throw new RuntimeException("이미 평가한 사람입니다.");
        }

        Member member = memberRepository.findById(memberId).get();
        Member target = memberRepository.findById(targetId).get();

        if (member == null || target == null) {
            throw new RuntimeException("평가자 또는 평가 대상이 존재하지 않습니다.");
        }

        // 기존에 상대가 나를 like 평가한적 있는지 확인
        Likes counterLike = likesRepository.findByMemberIdAndTargetIdAndStatus(targetId, memberId, true);


        Likes likes = new Likes(member, target, likeStatus);
        likesRepository.save(likes);

        // 1:1 매칭 결과
        if (counterLike == null) {
            return new OneToOneMatching(targetId, false);
        }
        return new OneToOneMatching(targetId, true);
    }

    public List<Likes> findClassificationList(Long memberId, Long targetId, boolean status) {

        System.out.println("MemberService.findClassificationList");
        System.out.println("memberId = " + memberId);
        System.out.println("targetId = " + targetId);
        System.out.println("status = " + status);

        // 보낸 사람(평가자)로 조회
        if (memberId != null && targetId == null) {
            return likesRepository.findByMemberIdAndStatus(memberId, status);
        }
        // 받는 사람(평가 대상)으로 조회
        else if (memberId == null && targetId != null) {
            return likesRepository.findByTargetIdAndStatus(targetId, status);
        }
        return null;
    }



    /**
     * 사용자 상세 정보 조회
     */
    public SearchDetailedMemberInfo findDetailedMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId).get();
        List<MemberImage> memberImages = memberImageRepository.findByMemberId(memberId);
        List<String> imageUrls = memberImages.stream()
                .map(mimg -> new String(mimg.getImage().getUrl()))
                .collect(Collectors.toList());

        return new SearchDetailedMemberInfo(member, imageUrls);
    }


    /**
     * 분류할 사용자들 찾기
     */
    public List<SearchDetailedMemberInfo> findSearchTargetGender(Long memberId, Gender gender, Long count) {

        // 기본값 10;
        if (count == null) {
            count = 10L;
        }
        // 최대값 50
        else if (count > 100) {
            count = 100L;
        }

        // 분류할 멤버 조회(좋아요, 싫어요)
        List<Member> members = memberRepository.findSearchTargetPage(memberId, gender, count.intValue());

        // 응답값 데이터
        List<SearchDetailedMemberInfo> searchDetailedMemberInfos = new ArrayList<>();
        for (Member m : members) {
            // 자기 자신은 조회되지 않도록
            if (m.getId() == memberId)
                continue;
            List<String> imgUrls = findProfileImagesUrls(m.getId());
            searchDetailedMemberInfos.add(new SearchDetailedMemberInfo(m, imgUrls));
        }

        return searchDetailedMemberInfos;
    }
}
