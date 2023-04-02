package com.yakgwa.catchme.domain;

import jakarta.persistence.*;
import lombok.Getter;

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

}
