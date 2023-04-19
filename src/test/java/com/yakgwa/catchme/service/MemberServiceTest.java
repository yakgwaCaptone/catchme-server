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

    @Test
    @DisplayName("평가 테스트")
    public void evaluationTest() {
        Member member1 = new Member("cccccccc", "010-9999-9999", "cccccccc@mail.com", "1999", Gender.MAN);
        Member member2 = new Member("dddddddd", "010-9999-9998", "dddddddd@mail.com", "2000", Gender.MAN);
        Member member3 = new Member("aaaaaaaa", "010-9999-9997", "aaaaaaaa@mail.com", "2001", Gender.WOMAN);
        Member target =  new Member("bbbbbbbb", "010-9999-9996", "bbbbbbbb@mail.com", "2002", Gender.MAN);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(target);

        Long targetId = target.getId();

        // 초기화
        em.flush();
        em.clear();

        memberService.evaluate(member1.getId(), target.getId(), 5);
        memberService.evaluate(member2.getId(), target.getId(), 3);
        memberService.evaluate(member3.getId(), target.getId(), 3);

        Member findTarget = memberService.findOne(targetId);

        assertThat(findTarget.getSumOfEvaluationScore()).isEqualTo(5 + 3 + 3); // 평가 점수 총합
        assertThat(findTarget.getNumberOfEvaluation()).isEqualTo(3); // 평가자 수
        assertThat(findTarget.getEverageScore()).isEqualTo((5 + 3 + 3) / (double)3); // 평균 점수 (주의 double 형변환 필요)

    }

    @Test
    @DisplayName("중복 평가 테스트")
    public void duplicateEvaluationTest() {
        // given
        Member member = new Member("aaaaaaaa", "010-9999-9997", "aaaaaaaa@mail.com", "2001", Gender.WOMAN);
        Member target =  new Member("bbbbbbbb", "010-9999-9996", "bbbbbbbb@mail.com", "2002", Gender.MAN);
        em.persist(member);
        em.persist(target);

        // 초기화
        em.flush();
        em.clear();

        // when
        memberService.evaluate(member.getId(), target.getId(), 5);

        // 현재 따로 예외 정의하지 않음
        // then
        Assertions.assertThrows(RuntimeException.class,
                () -> memberService.evaluate(member.getId(), target.getId(), 5));

    }

    @Test
    @DisplayName("평가 점수 범위 테스트")
    public void evaluationScoreRangeTest() {
        // given
        Member member1 = new Member("cccccccc", "010-9999-9999", "cccccccc@mail.com", "1999", Gender.MAN);
        Member member2 = new Member("dddddddd", "010-9999-9998", "dddddddd@mail.com", "2000", Gender.MAN);
        Member member3 = new Member("aaaaaaaa", "010-9999-9997", "aaaaaaaa@mail.com", "2001", Gender.WOMAN);
        Member target =  new Member("bbbbbbbb", "010-9999-9996", "bbbbbbbb@mail.com", "2002", Gender.MAN);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(target);

        Long targetId = target.getId();

        // 초기화
        em.flush();
        em.clear();

        // when
        memberService.evaluate(member1.getId(), target.getId(), 3);

        // then
        // 잘못된 범위 평가
        Assertions.assertThrows(RuntimeException.class,
                () -> memberService.evaluate(member2.getId(), target.getId(), 7) );

        Assertions.assertThrows(RuntimeException.class,
                () -> memberService.evaluate(member3.getId(), target.getId(), -1));

        // 점수 조회
        Member findTarget = memberService.findOne(targetId);
        assertThat(findTarget.getSumOfEvaluationScore()).isEqualTo(3); // 평가 점수 총합
        assertThat(findTarget.getNumberOfEvaluation()).isEqualTo(1); // 평가자 수
        assertThat(findTarget.getEverageScore()).isEqualTo(3 / (double)1); // 평균 점수

    }
}