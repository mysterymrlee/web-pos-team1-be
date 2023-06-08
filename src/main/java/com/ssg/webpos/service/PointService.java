package com.ssg.webpos.service;

import com.ssg.webpos.domain.Point;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.repository.PointRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {
  private final UserRepository userRepository;
  private final CartRedisRepository cartRedisRepository;
  private final PointRepository pointRepository;

  public int getPointAmount(Long userId) {
    Optional<Point> findPoint = pointRepository.findByUserId(userId);
    if (findPoint.isPresent()) {
      Point point = findPoint.get();
      int pointAmount = point.getPointAmount();
      return pointAmount;
    } else {
      throw new RuntimeException("사용자를 찾을 수 없습니다.");
    }
  }

  public int updatePoint(int totalPrice) {
    try {
        int earnedPoint = (int) (totalPrice * 0.001);
        return earnedPoint;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }
}