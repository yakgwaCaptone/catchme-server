package com.yakgwa.catchme.service;

import com.yakgwa.catchme.domain.Gender;
import com.yakgwa.catchme.domain.Image;
import com.yakgwa.catchme.domain.Member;
import com.yakgwa.catchme.domain.MemberImage;
import com.yakgwa.catchme.repository.ImageRepository;
import com.yakgwa.catchme.repository.MemberImageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
//@Rollback(value = false) // 데이터 확인하려면 주석 풀기 -> 대신 UnexpectedRollbackException 발생
class MemberServiceTest {
    @Autowired private MemberService memberService;
    @Autowired private MemberImageRepository memberImageRepository;
    @Autowired private ImageRepository imageRepository;
    @PersistenceContext
    EntityManager em;

    private Member createMember() {
        return new Member("test_user", "000-0000-0000",
                "test@mail.com", "2000", Gender.MAN);
    }

    @Test
    @DisplayName("프로필 이미지 추가 테스트")
    public void addProfileImageTest() throws Exception {
        // given
        Member member = createMember();
        Image image = new Image("test_image", member.getNickname());
        em.persist(member);
        em.persist(image);

        memberService.addProfileImage(member.getId(), image.getUrl());

        // when

        // then
    }

    @Test
    @DisplayName("프로필 이미지 추가 테스트2 (존재하지 않는 멤버 or url)")
    public void addProfileImageTest2() throws Exception {
        // given
        // when
        // then

        // 존재하지 않는 멤버ID or Url 저장 시도
        Assertions.assertThrows(
                java.util.NoSuchElementException.class,
                ()-> memberService.addProfileImage(1234L, "not_exist_url"));

    }

    @Test
    @DisplayName("프로필 이미지 삭제")
    public void deleteProfileImageTest() throws Exception {
        // given
        Member member = createMember();
        Image image = new Image("test_image", member.getNickname());
        em.persist(member);
        em.persist(image);

        MemberImage savedMemberImage = memberService.addProfileImage(member.getId(), image.getUrl());

        // when
        Long imageId = savedMemberImage.getImage().getId();
        memberService.deleteProfileImage(savedMemberImage);

        // then
        // 저장된 이미지 id로 조회 시도
        assertThat(imageRepository.findById(imageId).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("닉네임 변경 테스트")
    public void changeNicknameTest() throws Exception {
        // given
        Member member = createMember();
        em.persist(member);
        Long memberId = member.getId();

        em.flush();
        em.clear(); // 저장 후 초기화

        // when
        String nickname1 = "바뀔 이름1";
        String nickname2 = "바뀔 이름2";

        Member findMember = memberService.findOne(memberId);
        memberService.changeNickname(memberId, nickname1); // 닉네임 변경 -> 변경권 소진

        // then
        assertThat(findMember.getNickname()).isEqualTo(nickname1);
        Assertions.assertThrows(    // 예외 발생
               IllegalStateException.class,
                () -> memberService.changeNickname(memberId, nickname2) );

    }


    @Test
    @DisplayName("소개글 업데이트 테스트")
    public void updateIntroductionTest() throws Exception {
        // given
        Member member = createMember();
        em.persist(member);
        Long memberId = member.getId();

        em.flush();
        em.clear(); // 저장 후 초기화

        // when
        String introduction = "소개글 업데이트~~~";
        memberService.updateIntroduction(memberId, introduction);
        Member findMember = em.find(Member.class, memberId);

        // then
        assertThat(findMember.getIntroduction()).isEqualTo(introduction);
    }

   @Test
   @DisplayName("회원가입 테스트")
   public void joinTest() throws Exception {
       // given
       Member member = createMember();
       
       // when
       memberService.join(member);
       em.flush();
       em.clear();

       // then
       Member findMember = em.find(Member.class, member.getId());
       assertThat(findMember.getId()).isEqualTo(member.getId());
       assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
       assertThat(findMember.getPhoneNumber()).isEqualTo(member.getPhoneNumber());
       assertThat(findMember.getNickname()).isEqualTo(member.getNickname());
       assertThat(findMember.getIntroduction()).isEqualTo(member.getIntroduction());
       assertThat(findMember.getNumberOfEvaluation()).isEqualTo(member.getNumberOfEvaluation());
       assertThat(findMember.getCreatedDateTime()).isEqualTo(member.getCreatedDateTime());
       assertThat(findMember.getBirthYear()).isEqualTo(member.getBirthYear());
       assertThat(findMember.getSumOfEvaluationScore()).isEqualTo(member.getSumOfEvaluationScore());
   }

}