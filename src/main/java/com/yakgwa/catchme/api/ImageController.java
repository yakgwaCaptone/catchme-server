package com.yakgwa.catchme.api;

import com.yakgwa.catchme.domain.Image;
import com.yakgwa.catchme.dto.ImageResponseDto;
import com.yakgwa.catchme.dto.Result;
import com.yakgwa.catchme.repository.ImageRepository;
import com.yakgwa.catchme.service.ImageService;
import com.yakgwa.catchme.utils.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final S3Util s3Util;


    /**
     * AWS 이미지 업로드
     * 채팅시 사진 전송에 이용될 예정
     * Post 요청
     * multipart/form-data 형태
     */
    @PostMapping("/api/v1/images")
    public Result upload(
            Authentication authentication,
            @RequestParam("images")List<MultipartFile> imageFiles ) throws Exception {
        // 파라미터로 images 를 받음
        // 실질적으로 파일 형태로 받기에 변수명을 files로 변경 후 진행 가능

        log.info("/api/v1/images 호출");
        Long memberId = Long.parseLong(authentication.getName()); // jwt 인증 후 authentication에 멤버 id 저장됨
        List<Image> images = imageService.upload(memberId, imageFiles);
        // 이미지 업로드 결과 반환
        List<ImageResponseDto> collect = images.stream()
                .map(image -> new ImageResponseDto(image.getId(), image.getUrl()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    /**
     * 로컬에 저장된 사진 받아오기
     * 나중에 사진을 AWS S3에 업로드 이동하면 사용하지 않음
     * 아래 소스코드 원본
     * https://kkh0977.tistory.com/1202
     *
     * @param url
     * 호출 방법 /api/v1/images?url=이미지-업로드-후-받은-url-값
     * 예시) Get Method /api/v1/images?url=images/20230417/docker1681663748744.png
     */
//    @GetMapping("/api/v1/images")
//    public ResponseEntity<Resource> getImage(@PathParam("url") String url) {
//        String absolutePath = new File("").getAbsolutePath() + File.separator;
//        String fileUrl = absolutePath + url;
//
//        Resource resource = new FileSystemResource(fileUrl);
//        // 로컬 서버에 저장된 이미지 파일이 없을 경우
//        if(!resource.exists()){
//            System.out.println("FILE : NOT_FOUND");
//            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND); // 리턴 결과 반환 404
//        }
//
//
//        // 로컬 서버에 저장된 이미지가 있는 경우 로직 처리
//        HttpHeaders header = new HttpHeaders();
//        Path filePath = null;
//        try {
//            filePath = Paths.get(fileUrl);
//            // 인풋으로 들어온 파일명 .png / .jpg 에 맞게 헤더 타입 설정
//            header.add("Content-Type", Files.probeContentType(filePath));
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        // 이미지 리턴 실시 [브라우저에서 get 주소 확인 가능]
//        return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
//    }
}
