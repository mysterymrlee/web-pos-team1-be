package com.ssg.webpos.repository.settlement;

import com.ssg.webpos.domain.SettlementDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SettlementDayRepository extends JpaRepository<SettlementDay, Long> {
    //store_id별 조회(HQ 활용)
    // @Query("SELECT s FROM SettlementDay s where s.storeId = :storeId")
    List<SettlementDay> findByStoreId(Long storeId);
    //일별 조회(HQ 활용)
    List<SettlementDay> findBySettlementDate(LocalDate settlementDate);
    // store_id, 일별 조회(점장 활용)
    List<SettlementDay> findByStoreIdAndSettlementDate(Long storeId, LocalDate settlementDate);
}
