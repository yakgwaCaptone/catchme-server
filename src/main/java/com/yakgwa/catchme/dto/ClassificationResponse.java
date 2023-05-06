package com.yakgwa.catchme.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassificationResponse {
    private Long memberId;
    private Long targetId;
    private boolean status;
    private LocalDateTime createdDateTime;

    protected ClassificationResponse() {}
    public ClassificationResponse(Long memberId, Long targetId, boolean status, LocalDateTime createdDateTime) {
        this.memberId = memberId;
        this.targetId = targetId;
        this.status = status;
        this.createdDateTime = createdDateTime;
    }
}
