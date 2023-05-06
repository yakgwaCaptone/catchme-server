package com.yakgwa.catchme.dto;

import lombok.Data;

@Data
public class OneToOneMatching {
    private Long memberId;
    private boolean matchingStatus;

    public OneToOneMatching(Long memberId, boolean matchingStatus) {
        this.memberId = memberId;
        this.matchingStatus = matchingStatus;
    }
}
