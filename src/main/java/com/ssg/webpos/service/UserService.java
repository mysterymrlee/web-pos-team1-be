package com.ssg.webpos.service;

import com.ssg.webpos.config.jwt.JwtUtil;
import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.domain.enums.Role;
import com.ssg.webpos.dto.PointResponseDTO;
import com.ssg.webpos.dto.point.PointRequestDTO;
import com.ssg.webpos.repository.RefreshTokenRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRedisImplRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private JwtUtil jwtUtil;
  @Autowired
  private CartRedisImplRepository cartRedisImplRepository;

  public boolean checkMemberExistByPhoneNumber(String phoneNumber) {
    Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
    if (userOptional.isPresent()) {
      return true;
    }

    return false;
  }

  public boolean checkMemberExistByUserId(Long userId) {
    Optional<User> byId = userRepository.findById(userId);
    if (byId.isPresent()) {
      return true;
    }

    return false;
  }
  public PointResponseDTO login(User user, PosStoreCompositeId compositeId) {
    try {
      String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getPhoneNumber(), Role.NORMAL);
      String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getPhoneNumber(), Role.NORMAL);

      cartRedisImplRepository.saveToken(refreshToken, compositeId);
      return new PointResponseDTO(accessToken, refreshToken);
    } catch (Exception e) {
      e.printStackTrace();
      e.getStackTrace();
      throw new IllegalStateException();
    }

  }
}
