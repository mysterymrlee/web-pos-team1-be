package com.ssg.webpos.service;

import com.ssg.webpos.domain.User;
import com.ssg.webpos.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PointService {
  private final UserRepository userRepository;

  public PointService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void updatePoint(String phoneNumber, int finalPrice) {
    try {
      Optional<User> findUser = userRepository.findByPhoneNumber(phoneNumber);
      if (findUser.isPresent()) {
        User user = findUser.get();
        int currentPoint = user.getPoint();
        int point = (int) (finalPrice * 0.001);
        int updatedPoint = currentPoint + point;

        // point 값을 업데이트
        user.setPoint(updatedPoint);
        userRepository.save(user);
        System.out.println("Point updated for user: " + user.getId());
      } else {
        // 사용자를 찾을 수 없을 때 처리
        System.out.println("User not found for phoneNumber: " + phoneNumber);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
