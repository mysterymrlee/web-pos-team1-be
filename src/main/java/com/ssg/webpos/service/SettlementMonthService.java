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
    //월별 조회(HQ 활용)
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
    //store_id별,월별 조회(점장 활용)
    public List<SettlementMonth> selectByStoreIdAndDay(Long storeId, String SettlementDate) throws DateTimeParseException {
        try {
            LocalDate settlementDate = LocalDate.parse(SettlementDate);
            List<SettlementMonth> list = settlementMonthRepository.findByStoreIdAndSettlementDate(1L,settlementDate);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

    //store_id별, 기간별 월별 조회
    public List<SettlementMonth> selectByStoreIdAndDayBetween(Long storeId, String StartDate, String EndDate) throws DateTimeParseException {
        try {
            LocalDate start = LocalDate.parse(StartDate);
            LocalDate end = LocalDate.parse(EndDate);
            List<SettlementMonth> list = settlementMonthRepository.findByStoreIdAndSettlementDateBetween(1L,start,end);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

}
