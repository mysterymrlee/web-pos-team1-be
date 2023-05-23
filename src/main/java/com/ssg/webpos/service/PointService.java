package com.ssg.webpos.service;

import com.ssg.webpos.domain.User;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {
  private final UserRepository userRepository;
  private final CartRedisRepository cartRedisRepository;

  public int getUserPoint(Long storeId, Long posId) {
    Long userId = cartRedisRepository.findUserId(storeId + "-" + posId);
    if (userId != null) {
      Optional<User> userOptional = userRepository.findById(userId);
      if (userOptional.isPresent()) {
        User user = userOptional.get();
        return user.getPoint();
      }
    }
    return 0;
  }

  public int updatePoint(Long userId, int totalPrice) {
    try {
      Optional<User> findUser = userRepository.findById(userId);
      if (findUser.isPresent()) {
        User user = findUser.get();
        int currentPoint = user.getPoint();
        int point = (int) (totalPrice * 0.001);
        int updatedPoint = currentPoint + point;
        System.out.println("updatedPoint = " + updatedPoint);

        // point 값을 업데이트
        user.setPoint(updatedPoint);
        userRepository.save(user);
        return point;
      } else {
        System.out.println("user 찾지 못했습니다.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public void deductPoints(Long userId, int deductedAmount) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    int currentPoint = user.getPoint();
    if (currentPoint < deductedAmount) {
      throw new RuntimeException("포인트가 부족합니다.");
    }

    int updatedPoint = currentPoint - deductedAmount;
    user.setPoint(updatedPoint);
    userRepository.save(user);
  }

}
