package com.yakgwa.catchme.service;

import com.yakgwa.catchme.domain.Image;
import com.yakgwa.catchme.repository.ImageRepository;
import com.yakgwa.catchme.utils.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final FileHandler fileHandler;

    public List<Image> upload(Long memberId, List<MultipartFile> imageFiles) throws IOException {
        // file handler를 통해 NultipartFile : imageFiles 를 분석해서 image 형태로 받는다.
        List<Image> images = fileHandler.parseImageFileInfo(memberId, imageFiles);

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
