package com.yakgwa.catchme.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ImageResponseDto {
    private Long imageId;
    private String url;

    public ImageResponseDto(Long imageId, String url) {
        this.imageId = imageId;
        this.url = url;
    }
}
