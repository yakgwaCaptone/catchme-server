package com.yakgwa.catchme.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 상태 코드 추가
public class MemberDataNotLoadException extends RuntimeException {
    public MemberDataNotLoadException(String message) {
        super(message);
    }

}
