package com.yakgwa.catchme.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class MemberSns {
    @Id
    @GeneratedValue
    @Column(name = "member_sns_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 헷갈리게 되어 있긴 한데
     * SNS_ID
     * ex) @user_id._.
     */
    private String snsId;
    private String url;
    @Enumerated(value = EnumType.STRING)
    private SnsType snsType;


    protected MemberSns() {}    // 기본 생성자
    public MemberSns(Member member, String snsId, String url, SnsType snsType) {
        this.member = member;
        this.snsId = snsId;
        this.url = url;
        this.snsType = snsType;
    }
}