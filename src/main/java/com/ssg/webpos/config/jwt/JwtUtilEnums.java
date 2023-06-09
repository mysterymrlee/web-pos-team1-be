package com.ssg.webpos.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum JwtUtilEnums {
    TOKEN_PREFIX("JWT 타입 / Bearer ", "Bearer "),
    HEADER_STRING("JWT 헤터 / Authorization", "Authorization");

    private String description;
    private String value;
}
