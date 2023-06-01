package com.ssg.webpos.repository;

import com.ssg.webpos.domain.PointSaveHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PointSaveHistoryRepository extends JpaRepository<PointSaveHistory, Long> {
  Optional<PointSaveHistory> findByOrderId(Long orderId);
  List<PointSaveHistory> findByExpiredDateBefore(LocalDateTime currentDate);
}
