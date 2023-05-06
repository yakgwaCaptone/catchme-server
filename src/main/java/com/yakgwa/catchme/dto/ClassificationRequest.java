package com.yakgwa.catchme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ClassificationRequest {
    private Long memberId;
    private Long targetId;
    private boolean status;

    protected ClassificationRequest() {}
    public ClassificationRequest(Long memberId, Long targetId, boolean status) {
        this.memberId = memberId;
        this.targetId = targetId;
        this.status = status;
    }

}
