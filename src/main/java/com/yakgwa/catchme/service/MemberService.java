package com.yakgwa.catchme.service;

import com.yakgwa.catchme.domain.Image;
import com.yakgwa.catchme.domain.Member;
import com.yakgwa.catchme.domain.MemberImage;
import com.yakgwa.catchme.repository.ImageRepository;
import com.yakgwa.catchme.repository.MemberImageRepository;
import com.yakgwa.catchme.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberImageRepository memberImageRepository;
    private final ImageRepository imageRepository;

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
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }
    }

    @Transactional
    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).get();
    }

    /**
     * 외부에서 사진 저장 후 저장된 경로 url 이용
     * 멤버Id, url 로 조회 후 저장
     * 프로필 사진 추가
     */
    @Transactional
    public MemberImage addProfileImage(Long memberId, String imageUrl) {
        Image image = imageRepository.findByUrl(imageUrl).get();
        Member member = memberRepository.findById(memberId).get();
        MemberImage memberImage = new MemberImage(member, image);

        return memberImageRepository.save(memberImage);
    }


    @Transactional
    public void deleteProfileImage(MemberImage memberImage) {
        Long imageId = memberImage.getImage().getId();
        memberImageRepository.delete(memberImage);  // 멤버 이미지 삭제
        imageRepository.deleteById(imageId);        // 이미지 삭제
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

    public Member findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }
}
