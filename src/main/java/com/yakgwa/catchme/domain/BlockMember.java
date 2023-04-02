package com.yakgwa.catchme.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class BlockMember {
    @Id @GeneratedValue
    @Column(name = "block_member_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Member target;

    private LocalDateTime createdDateTime;


    protected BlockMember() { }
    public BlockMember(Member member, Member target) {
        this.member = member;
        this.target = target;
        this.createdDateTime = LocalDateTime.now();
    }
}

