package com.ssg.webpos.repository.settlement;

import com.ssg.webpos.domain.SettlementDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SettlementDayRepository extends JpaRepository<SettlementDay, Long> {
    //store_id별 조회(HQ 활용)
    // @Query("SELECT s FROM SettlementDay s where s.storeId = :storeId")
    List<SettlementDay> findByStoreId(Long storeId);
    //일별 조회(HQ 활용)
    List<SettlementDay> findBySettlementDate(LocalDate settlementDate);
    // store_id, 일별 조회(점장 활용)
    List<SettlementDay> findByStoreIdAndSettlementDate(Long storeId, LocalDate settlementDate);
    // 기간별 조회
    List<SettlementDay> findByStoreIdAndSettlementDateBetween(Long storeId, LocalDate StartDate, LocalDate EndDate);
}
