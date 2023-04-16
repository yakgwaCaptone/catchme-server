package com.yakgwa.catchme.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ImageUploadResponseDto {
    private Long imageId;
    private String url;

    public ImageUploadResponseDto(Long imageId, String url) {
        this.imageId = imageId;
        this.url = url;
    }
}
