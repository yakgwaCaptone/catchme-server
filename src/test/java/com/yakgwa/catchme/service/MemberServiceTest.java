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
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
//@Rollback(value = false)
// 데이터 확인하려면 Rollback 주석 풀기 -> 대신 UnexpectedRollbackException 발생으로 인해 실패 뜨며 강제 롤백됨.
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
    @DisplayName("중복된 닉네임 변경 테스트")
    public void changeDuplicateNicknameTest() throws Exception {
        // given
        Member member = createMember();
        Member member2 = new Member("중복닉네임", "000-1234-5678",
                "dupli@mail.com", "1988", Gender.MAN);
        em.persist(member);
        em.persist(member2);
        Long memberId = member.getId();

        em.flush();
        em.clear(); // 저장 후 초기화

        // when
        String nickname = "중복닉네임";

        Member findMember = memberService.findOne(memberId);

        Assertions.assertThrows( // 중복된 닉네임으로 변경 -> 예외 발생
                RuntimeException.class,
                () -> memberService.changeNickname(memberId, nickname));

        // then
        /*
        assertThat(findMember.getNickname()).isEqualTo(nickname1);
                IllegalStateException.class,
        */

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
        try {
            Assertions.assertThrows(    // 예외 발생
                    IllegalStateException.class,
                    () -> memberService.changeNickname(memberId, nickname2));
        } catch (UnexpectedRollbackException e) {

        }

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