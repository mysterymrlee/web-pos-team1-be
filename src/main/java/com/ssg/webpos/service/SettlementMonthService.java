package com.ssg.webpos.service;

import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class SettlementMonthService {
    private final SettlementMonthRepository settlementMonthRepository;

    //store_id별 조회(HQ 활용)
    public List<SettlementMonth> selectByStoreId(Long storeId) throws IllegalStateException {
        try {
            List<SettlementMonth> list = settlementMonthRepository.findByStoreId(storeId);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    //일별 조회(HQ 활용)
    public List<SettlementMonth> selectByDay(String settlementDate) throws DateTimeParseException {
       try{
            LocalDate localDate = LocalDate.parse(settlementDate);
            List<SettlementMonth> list = settlementMonthRepository.findBySettlementDate(localDate);
            return list;
       } catch (Exception e) {
           e.printStackTrace();
           System.out.println(e);
           return Collections.emptyList();
       }
    }
    //store_id별,일별 조회(점장 활용)
    public List<SettlementMonth> selectByStoreIdAndDay(Long storeId, String settlementDate) throws DateTimeParseException {
        try {
            LocalDate localDate = LocalDate.parse(settlementDate);
            List<SettlementMonth> list = settlementMonthRepository.findByStoreIdAndSettlementDate(storeId,localDate);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }
}
