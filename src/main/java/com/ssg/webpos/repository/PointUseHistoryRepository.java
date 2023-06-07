package com.ssg.webpos.repository;

import com.ssg.webpos.domain.PointUseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointUseHistoryRepository extends JpaRepository<PointUseHistory, Long> {
    Optional<PointUseHistory> findByOrderId(Long orderId);

    /**
     * save()될 때마다 point테이블에 update 쿼리 나가는 trigger 실행됨
     * */
    PointUseHistory save(PointUseHistory pointUseHistory);

}
