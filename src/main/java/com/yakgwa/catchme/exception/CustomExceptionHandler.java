package com.yakgwa.catchme.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // 예외 처리를 해주기 위함
public class CustomExceptionHandler {

    /**
     *
     * ExceptionHandler 어노테이션에 설정해둔 예외가 발생하면
     * 아래 메서드에서 에러를 처리해주는 것 같다.
     */
    @ExceptionHandler(MemberDataNotLoadException.class)
    public ResponseEntity<Map<String, String>> MemberDataNotLoadException(Exception e) {
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus httpStatus= HttpStatus.NOT_FOUND;

        //logger.info("MemberDataNotLoadException Exception Handler 호출");
        Map<String, String> map = new HashMap<>();
        map.put("error type", httpStatus.getReasonPhrase());
        map.put("code", "404");
        map.put("message", e.getMessage());

        return new ResponseEntity<>(map, responseHeaders, httpStatus);
    }

    /**
     * 닉네임 조회 api 에서 예외 발생(해당 닉네임이 있을 경우 exception)
     */
    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<ErrorResult> DuplicateNicknameException(DuplicateNicknameException e) {
        ErrorResult errorResult = new ErrorResult("DuplicateNicknameException", e.getMessage());
        return new ResponseEntity<>(errorResult, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    /**
     * 닉네임 변경권 없음 예외
     */
    @ExceptionHandler(HasNotNicknameChangeCouponException.class)
    public ResponseEntity<ErrorResult> HasNotNicknameChangeCouponException(HasNotNicknameChangeCouponException e) {
        ErrorResult errorResult = new ErrorResult("HasNotNicknameChangeCouponException", e.getMessage());
        return new ResponseEntity<>(errorResult, new HttpHeaders(), HttpStatus.PRECONDITION_FAILED);
    }
}
