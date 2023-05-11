package com.ssg.webpos.repository.settlement;


import com.ssg.webpos.domain.SettlementMonth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SettlementMonthRepository extends JpaRepository<SettlementMonth, Long> {
    //store_id별 조회(HQ 활용)
    // @Query("SELECT s FROM SettlementDay s where s.storeId = :storeId")
    List<SettlementMonth> findByStoreId(Long storeId);
    //일별 조회(HQ 활용)
    List<SettlementMonth> findBySettlementDate(LocalDate settlementDate);
    // store_id, 일별 조회(접장 활용)
    List<SettlementMonth> findByStoreIdAndSettlementDate(Long storeId, LocalDate settlementDate);
}
