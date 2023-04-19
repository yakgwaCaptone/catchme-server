package com.yakgwa.catchme.service;

import com.yakgwa.catchme.domain.Evaluation;
import com.yakgwa.catchme.domain.Image;
import com.yakgwa.catchme.domain.Member;
import com.yakgwa.catchme.domain.MemberImage;
import com.yakgwa.catchme.dto.ImageResponseDto;
import com.yakgwa.catchme.dto.MemberUpdateRequestDto;
import com.yakgwa.catchme.dto.MemberUpdateResponseDto;
import com.yakgwa.catchme.exception.DuplicateNicknameException;
import com.yakgwa.catchme.repository.EvaluationRepository;
import com.yakgwa.catchme.repository.ImageRepository;
import com.yakgwa.catchme.repository.MemberImageRepository;
import com.yakgwa.catchme.repository.MemberRepository;
import com.yakgwa.catchme.utils.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberImageRepository memberImageRepository;
    private final FileHandler fileHandler;
    private final EvaluationRepository evaluationRepository;
    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);    // 중복 회원 검사
        isDuplicationNickname(member.getNickname()); // 중복 닉네임 검사
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 검사 (기준 : 휴대폰 번호)

    /**
     * 중복 회원 검사 (기준 : 휴대폰 번호)
     */
    private boolean validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByPhoneNumber(member.getPhoneNumber());
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

        Member member = memberRepository.findById(memberId).get();
        // 변경 사항에 닉네임이 있을 때
        if (StringUtils.hasText(nickname)) {
            isDuplicationNickname(nickname);
            member.changeNickname(nickname);
        }

        member.setIntroduction(introduction);
        return new MemberUpdateResponseDto(member.getNickname(), member.getIntroduction());
    }


    public Member findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }


    @Transactional
    public List<MemberImage> uploadProfileImage(Long memberId, List<MultipartFile> imageFiles) throws IOException {
        // file handler를 통해 MultipartFile : imageFiles 를 분석해서 image 형태로 받는다.
        List<Image> images = fileHandler.parseImageFileInfo(memberId, imageFiles);

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

    public List<ImageResponseDto> findProfileImages(Long memberId) {
        return memberImageRepository.findByMemberId(memberId)
                .stream()
                .map(memberImage -> new ImageResponseDto(memberImage.getId(), memberImage.getImage().getUrl()))
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
}
