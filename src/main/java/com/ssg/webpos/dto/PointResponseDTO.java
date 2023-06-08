package com.ssg.webpos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointResponseDTO {
    private String accessToken;
    private String refreshToken;
}
