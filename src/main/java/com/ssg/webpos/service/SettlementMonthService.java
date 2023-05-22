package com.ssg.webpos.service;

import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.dto.settlement.SettlementMonthReportDTO;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
    public List<SettlementMonth> selectByStoreIdAndDay(Long storeId, String settlementDate) throws DateTimeParseException {
        try {
            LocalDate SettlementDate = LocalDate.parse(settlementDate);
            List<SettlementMonth> list = settlementMonthRepository.findByStoreIdAndSettlementDate(1L,SettlementDate);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

    // store_id, year 별 월별정산내역 조회
    // 1L, "2023" 받을시 store_id=1L인 가게의 2023년 월별정산내역 조회
    public List<SettlementMonth> selectByStoreIdAndDayBetween(Long storeId, String year) throws DateTimeParseException {
        try {
            LocalDate StartDate = LocalDate.of(Integer.parseInt(year), Month.JANUARY,1);
            LocalDate EndDate = LocalDate.of(Integer.parseInt(year), Month.DECEMBER,31);
            List<SettlementMonth> list = settlementMonthRepository.findByStoreIdAndSettlementDateBetween(storeId,StartDate,EndDate);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

    // year 받으면 해당 월별정산내역 조회
    // SettlementMonth(id=132, settlementPrice=500000000, settlementDate=2023-11-01) 가 불러와진다.
    public List<SettlementMonth> selectByYear(String year) {
        try {
            LocalDate StartDate = LocalDate.of(Integer.parseInt(year), Month.JANUARY,1);
            LocalDate EndDate = LocalDate.of(Integer.parseInt(year), Month.DECEMBER,31);
            List<SettlementMonth> list = settlementMonthRepository.findBySettlementDateBetween(StartDate,EndDate);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

    // 기간별 전체 월별정산내역 조회
    // "yyyy-mm" 을 받아온다.
    // ex. "2023-02","2023-03" 받으면 2023-02부터 2023-03까지의 월별정산내역 조회
    public List<SettlementMonth> selectByMonthRange(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate end = LocalDate.parse(endDate + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
            List<SettlementMonth> list = settlementMonthRepository.findBySettlementDateBetween(start,end);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

    // 기간별 store_id별 월별정산내역 조회
    public List<SettlementMonth> selectByStoreIdAndMonthRange(Long storeId, String startDate, String endDate) throws DateTimeParseException {
        try {
            LocalDate start = LocalDate.parse(startDate + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate end = LocalDate.parse(endDate + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
            List<SettlementMonth> list = settlementMonthRepository.findByStoreIdAndSettlementDateBetween(storeId,start,end);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

}
