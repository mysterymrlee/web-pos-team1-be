package com.ssg.webpos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_PASSWORD(NOT_FOUND, "패스워드가 잘못되었습니다." ),
    INCORRECT_PASSWORD_CORRECT(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USERNAME_NOT_FOUNDED(NOT_FOUND, "해당 사용자는 없습니다." ),
    INVALID_PERMISSION(UNAUTHORIZED, "사용자가 권한이 없습니다."),
    DATABASE_ERROR(INTERNAL_SERVER_ERROR, "DB에러");

    private HttpStatus status;
    private String message;
}
