package com.yakgwa.catchme.utils;

import com.yakgwa.catchme.domain.Image;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 이미지 관련 기능은
 * https://velog.io/@pyo-sh/Spring-Boot-파일이미지-업로드-구현하기
 * 해당 게시글을 토대로 구현함
 */

@Component
public class FileHandler {

    // 이용 가능한 타입
    private String availableTypes[] = {"image/jpg", "image/jpeg", "image/png", "image/gif"};
    // 그에 맞는 확장자
    private String extensions[] = {".jpg", ".jpg", ".png", ".gif"};



    // Todo 아직 리팩터링이 더 필요해보이나 시간 관계상 추후 작업
    public List<Image> parseImageFileInfo(Long memberId, List<MultipartFile> imageFiles) throws IOException {
        // 반환할 이미지 객체들
        List<Image> images = new ArrayList<>();

        // 빈 파일
        if (imageFiles.isEmpty()) {
            return images;
        }

        String absolutePath = getAbsolutePath(); // 절대 경로
        String path = getPathToSave(); // 저장할 경로(상대 경로)

        // 파일 처리
        for (MultipartFile imageFile : imageFiles) {
            // 이미지 공백
            if (imageFile.isEmpty())
                continue;

            String contentType = imageFile.getContentType();
            String originalFileExtension = null;

            // 확장자가 없음
            if (!StringUtils.hasText(contentType))
                break;

            // 확장자 처리
            for (int i = 0; i < availableTypes.length; i++) {
                if (availableTypes[i].contains(contentType)){
                    originalFileExtension = extensions[i];
                    break;
                }
            }

            // 확장자 지정되지 않음
            if (!StringUtils.hasText(originalFileExtension)) {
                break;
            }

            // 파일 이름 지정 = 원본 이름(확장자 제거) + 시간 + 확장자
            String fileName = getExtensionRemovedOriginalFilename(imageFile) +
                    Long.toString(System.currentTimeMillis()) +
                    originalFileExtension;
            String url = path + "/" + fileName;

            // 원래는 created by 를 닉네임으로 저장하려 했으나 memberId로 저장
            images.add(new Image(url, Long.toString(memberId)));


            // 해석이 좀 애매함
            // 파일로 변경하여 저장
            File file = new File(absolutePath + path + "/" + fileName);
            imageFile.transferTo(file);
        }

        return images;
    }

    /**
     * 확장자 제거한 파일 이름 반환
     */
    private String getExtensionRemovedOriginalFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int dotIndex = file.getOriginalFilename().lastIndexOf(".");
        String filename = originalFilename.substring(0, dotIndex);
        return filename;
    }

    /**
     * 저장할 경로(상대 경로)
     */
    private String getPathToSave() {
        // 파일 분류 = 업로드한 날짜
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = simpleDateFormat.format(new Date()); // Date()를 위에서 정의한 패턴으로 변경

        // 경로 지정, 해당 위치에 저장
        String path = "images/" + currentDate;
        File file = new File(path);

        // 저장할 위치의 디렉토리 존재하지 않음
        if (!file.exists()) {
            // 디렉토리 생성 (상위 디렉토리 없으면 상위 dir 생성)
            file.mkdirs();
        }

        return path;
    }

    private String getAbsolutePath() {
        // 프로젝트 폴더에 저장하기 위해 절대경로를 설정 (Window 의 Tomcat 은 Temp 파일을 이용한다)
        // windows 원래 "\\",  mac "/"  구분자 다르다.
        // File.separator 를 통해 각 OS에 맞게 구분자 붙이기
        //String absolutePath = new File("").getAbsolutePath() + "/";
        String absolutePath = new File("").getAbsolutePath() + File.separator;
        return absolutePath;
    }
}
