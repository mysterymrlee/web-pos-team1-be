package com.ssg.webpos.service;

import com.ssg.webpos.config.jwt.JwtUtil;
import com.ssg.webpos.domain.HQAdmin;
import com.ssg.webpos.domain.enums.Role;
import com.ssg.webpos.dto.HQAdminLoginRequestDTO;
import com.ssg.webpos.dto.HQAdminLoginResponseDTO;
import com.ssg.webpos.dto.RefreshTokenDto;
import com.ssg.webpos.repository.HQAdminRepository;
import com.ssg.webpos.repository.cart.CartRedisImplRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HQAdminService {
    private final JwtUtil jwtUtil;
    private final HQAdminRepository hqAdminRepository;
    private final CartRedisImplRepository cartRedisImplRepository;
    public HQAdminLoginResponseDTO login(HQAdminLoginRequestDTO requestDTO) throws UserPrincipalNotFoundException {
        Optional<HQAdmin> findhqAdmin = hqAdminRepository.findByAdminNumber(requestDTO.getAdminNumber());
        if (findhqAdmin.isEmpty()) {
            throw new UserPrincipalNotFoundException("존재하지 않는 HQ admin입니다.");
        }
        // requestDTO.getAdminNumber()에 해당하는 직원번호를 가진 HQ Admin 가 존재하는 경우
        HQAdmin hqAdmin = findhqAdmin.get();
        String accessToken = jwtUtil.generateAccessToken(hqAdmin.getId(), hqAdmin.getAdminNumber(), Role.ROLE_HQ);
        String refreshToken = jwtUtil.generateRefreshToken(hqAdmin.getId(), hqAdmin.getAdminNumber(), Role.ROLE_HQ);

        HQAdminLoginResponseDTO responseDTO = new HQAdminLoginResponseDTO(accessToken, refreshToken);
        return responseDTO;
    }
}
