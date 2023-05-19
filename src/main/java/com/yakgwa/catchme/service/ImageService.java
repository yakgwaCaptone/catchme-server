package com.yakgwa.catchme.service;

import com.yakgwa.catchme.domain.Image;
import com.yakgwa.catchme.repository.ImageRepository;
import com.yakgwa.catchme.utils.FileHandler;
import com.yakgwa.catchme.utils.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final FileHandler fileHandler;
    private final S3Util s3Util;

    public List<Image> upload(Long memberId, List<MultipartFile> imageFiles) throws IOException {
        List<String> urls = s3Util.upload(imageFiles);
        List<Image> images = urls.stream()
                .map(url -> new Image(url, memberId.toString()))
                .collect(Collectors.toList());

        if (images.isEmpty()) {
            // Todo 파일 없을 때 throw 예외 및 예외처리
            return null;
        }

        // 이미지 저장
        for (int i = 0; i < images.size(); i++) {
            images.set(i, imageRepository.save(images.get(i)));
        }

        // 반환
        return images;
    }
}
