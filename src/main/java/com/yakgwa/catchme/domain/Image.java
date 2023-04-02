package com.yakgwa.catchme.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Image {
    @Id @GeneratedValue
    @Column(name = "image_id")
    private Long id;
    private String url;
    private LocalDateTime createdDateTime;

    private String createdBy;


    /**
     * 실제 이미지 업로드 구현 후 달라질 수 있음
     */
    protected Image() {}
    public Image(String url, String createdBy) {
        this.url = url;
        this.createdDateTime = LocalDateTime.now();
        this.createdBy = createdBy;
    }
}
