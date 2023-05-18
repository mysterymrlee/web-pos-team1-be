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

  public void updatePoint(String phoneNumber, int totalPrice) {
    try {
      Optional<User> findUser = userRepository.findByPhoneNumber(phoneNumber);
      if (findUser.isPresent()) {
        User user = findUser.get();
        int currentPoint = user.getPoint();
        int point = (int) (totalPrice * 0.001);
        int updatedPoint = currentPoint + point;

        // point 값을 업데이트
        user.setPoint(updatedPoint);
        userRepository.save(user);
        System.out.println(user.getId());
      } else {
        System.out.println("찾을 수 없습니다.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
