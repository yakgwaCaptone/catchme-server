package com.yakgwa.catchme.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Likes {
    @Id @GeneratedValue
    @Column(name = "likes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Member target;

    private boolean status;
    private LocalDateTime createdDateTime;

    protected Likes() { }

    public Likes(Member member, Member target, boolean status) {
        this.member = member;
        this.target = target;
        this.status = status;
        this.createdDateTime = LocalDateTime.now();
    }

}
