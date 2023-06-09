package com.ssg.webpos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HQAdminLoginRequestDTO {
    private String adminNumber; // 직원번호
    private String password; // 비밀번호
}
