package com.yakgwa.catchme.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class MemberImage {

    @Id @GeneratedValue
    @Column(name = "member_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;
}
