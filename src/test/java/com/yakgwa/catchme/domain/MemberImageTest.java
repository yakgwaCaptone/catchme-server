package com.yakgwa.catchme.domain;

import com.yakgwa.catchme.repository.MemberImageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberImageTest {

    @Autowired private MemberImageRepository memberImageRepository;
    @PersistenceContext private EntityManager em;

    @Test
    @DisplayName("멤버 이미지 저장")
    public void testEntity() throws Exception {
        // given
        // 멤버, 이미지 생성 및 저장
        String nickname = "member123";
        Member member = new Member(nickname, "000-0000-0000",
                "member123@mail.com", "1998", Gender.MAN);
        em.persist(member);

        Image image = new Image("image_url_gggg", nickname);
        em.persist(image);

        MemberImage memberImage = new MemberImage(member, image);
        em.persist(memberImage);
        Long memberImageId = memberImage.getId();

        // 초기화
        em.flush();
        em.clear();

        // when
        MemberImage findMemberImage = memberImageRepository.findById(memberImageId).get();

        // then
        org.assertj.core.api.Assertions.assertThat(findMemberImage.getMember().getId()).isEqualTo(member.getId());
        org.assertj.core.api.Assertions.assertThat(findMemberImage.getImage().getId()).isEqualTo(image.getId());
    }
}