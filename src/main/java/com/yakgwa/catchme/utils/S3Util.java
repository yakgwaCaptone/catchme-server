package com.yakgwa.catchme.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class S3Util {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String extensions[] = {".jpg", ".jpg", ".png", ".gif"};


    /**
     * MultipartFile List 전달받아 S3에 업로드 후
     * 이미지 url 리스트 반환
     */
    public List<String> upload(List<MultipartFile> multipartFiles) throws IOException {
        String currentDate = getCurrentDateStr();
        List<String> imageUrls = new ArrayList<>();

        // MultipartFiles 순회
        for(MultipartFile multipartFile: multipartFiles) {
            try {
                String imageUrl = uploadToS3(currentDate, multipartFile);
                if (StringUtils.hasText(imageUrl)) {
                    imageUrls.add(imageUrl);
                }
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패 : " + multipartFile.getOriginalFilename());
            }
        }

        return imageUrls;
    }


    /**
     * MultipartFile AWS S3로 업로드
     * return => 업로드된 이미지 Url
     */
    private String uploadToS3(String currentDate, MultipartFile multipartFile) throws IOException {
        String filename = createFilename(currentDate, multipartFile.getOriginalFilename());

        // 파일 확장자 확인
        if (!validateFileExtension(filename)) {
            return null;
        }

        // 파일 메타데이터 설정
        long size = multipartFile.getSize(); // 파일 크기
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(size);

        // S3에 업로드
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, filename, multipartFile.getInputStream(), objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        String imageUrl = amazonS3Client.getUrl(bucket, filename).toString(); // 접근가능한 URL 가져오기
        return  imageUrl;
    }

    private String getCurrentDateStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = simpleDateFormat.format(new Date()); // Date()를 위에서 정의한 패턴으로 변경
        return currentDate;
    }

    private String getFileExtension(String filename) {
        int fileExtensionIndex = filename.lastIndexOf(".");
        String fileExtension = filename.substring(fileExtensionIndex);
        return fileExtension;
    }


    private String createFilename(String currentDate, String originalFilename) {
        int fileExtensionIndex = originalFilename.lastIndexOf(".");
        String filename = originalFilename.substring(0, fileExtensionIndex);
        String fileExtension = originalFilename.substring(fileExtensionIndex);
        String now = String.valueOf(System.currentTimeMillis());

        return currentDate + "/" + filename + "_" + now + fileExtension;
    }

    private boolean validateFileExtension(String filename) {
        String fileExtension = getFileExtension(filename);
        for (String extension : extensions) {
            if(extension.equals(fileExtension))
                return true;
        }
        return false;
    }

}