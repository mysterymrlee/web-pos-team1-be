package com.ssg.webpos.repository;

import com.ssg.webpos.domain.PointUseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointUseHistoryRepository extends JpaRepository<PointUseHistory, Long> {
    Optional<PointUseHistory> findByOrderId(Long orderId);

}
