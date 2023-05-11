package com.ssg.webpos.service;

import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional // readOnly = true
public class SettlementDayService {
    private final SettlementDayRepository settlementDayRepository;

    //store_id별 조회(HQ 활용)
    public List<SettlementDay> selectByStoreId(Long storeId) {
        try {
            List<SettlementDay> list = settlementDayRepository.findByStoreId(storeId);
            return list;
        }catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    //일별 조회(HQ 활용)
    public List<SettlementDay> selectByDay(String settlementDate) throws DateTimeParseException {
        try{
            LocalDate localDate = LocalDate.parse(settlementDate);
            List<SettlementDay> list = settlementDayRepository.findBySettlementDate(localDate);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

    //store_id별,일별 조회(점장 활용)
    public List<SettlementDay> selectByStoreIdAndDay(Long storeId, String settlementDate) throws DateTimeParseException {
        try{
            LocalDate localDate = LocalDate.parse(settlementDate);
            List<SettlementDay> list = settlementDayRepository.findByStoreIdAndSettlementDate(storeId,localDate);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }
}
