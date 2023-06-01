package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Point;
import com.ssg.webpos.domain.PointSaveHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
  Optional<Point> findByUserId(Long userId);
}
