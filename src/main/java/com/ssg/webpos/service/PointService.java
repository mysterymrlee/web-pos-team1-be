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
    if(findPoint.isPresent()) {
      Point point = findPoint.get();
      int pointAmount = point.getPointAmount();
      return pointAmount;
    } else {
      throw new RuntimeException("사용자를 찾을 수 없습니다.");
    }
  }
  public int updatePoint(Long userId, int totalPrice) {
    try {
      Optional<User> findUser = userRepository.findById(userId);
      if (findUser.isPresent()) {
        User user = findUser.get();
        Point point = user.getPoint();

        if (point == null) {
          point = new Point();
          point.setUser(user);
        }

        int currentPointAmount = point.getPointAmount();
        int earnedPoint = (int) (totalPrice * 0.001);
        int updatedPointAmount = currentPointAmount + earnedPoint;
        System.out.println("updatedPointAmount = " + updatedPointAmount);

        // pointAmount 값을 업데이트
        point.setPointAmount(updatedPointAmount);
        pointRepository.save(point);
        return earnedPoint;
      } else {
        System.out.println("사용자를 찾을 수 없습니다.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }
  @Transactional
  public void deductPoints(Long userId, int deductedAmount) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    Point point = user.getPoint();
    int currentPoint = point.getPointAmount();
    System.out.println("deductPoints currentPoint = " + currentPoint);

    if (currentPoint < deductedAmount) {
      throw new RuntimeException("포인트가 부족합니다.");
    }

    int updatedPoint = currentPoint - deductedAmount;
    System.out.println("updatedPoint = " + updatedPoint);

    point.setPointAmount(updatedPoint);
    pointRepository.save(point);

    userRepository.save(user);

  }
}