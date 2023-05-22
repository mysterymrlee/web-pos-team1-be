package com.ssg.webpos.service;

import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional // readOnly = true
public class SettlementDayService {
    private final SettlementDayRepository settlementDayRepository;

    //store_id별 조회(HQ 활용)
    public List<SettlementDay> selectByStoreId(Long storeId) throws IllegalArgumentException{
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

    //기간별 조회(점장 활용) 특정 store_id 정산내역만 확인 가능
    public List<SettlementDay> selectByStoreIdAndDayBetween(Long StoreId, String start, String end) throws DateTimeParseException {
        try {
            LocalDate StartDate = LocalDate.parse(start);
            LocalDate EndDate = LocalDate.parse(end);
            List<SettlementDay> list = settlementDayRepository.findByStoreIdAndSettlementDateBetween(StoreId,StartDate,EndDate);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

    // year, month 받으면 해당 일별정산내역 조회("yyyy-mm")
    // "2023-02" 받으면 해당 날짜의 일별정산내역 전체 조회
    public List<SettlementDay> selectByYearMonth(String date) {
        try {
            LocalDate startDate = LocalDate.parse(date+"-01");
            YearMonth yearMonth = YearMonth.parse(date);
            int lastDayOfMonth = yearMonth.lengthOfMonth();
            String ParsedDateString = date + "-" + lastDayOfMonth;
            LocalDate endDate = LocalDate.parse(ParsedDateString);
            List<SettlementDay> list = settlementDayRepository.findBySettlementDateBetween(startDate,endDate);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

    // "yyyy-mm" 형식 date,storeId 받으면 해당 일별정산내역 조회("yyyy-mm")
    // "2023-02" 받으면 해당 날짜의 일별정산내역 전체 조회
    public List<SettlementDay> selectByYearMonthAndStoreId(String date, Long storeId) {
        try {
            LocalDate startDate = LocalDate.parse(date+"-01");
            YearMonth yearMonth = YearMonth.parse(date);
            int lastDayOfMonth = yearMonth.lengthOfMonth();
            String ParsedDateString = date + "-" + lastDayOfMonth;
            LocalDate endDate = LocalDate.parse(ParsedDateString);
            List<SettlementDay> list = settlementDayRepository.findByStoreIdAndSettlementDateBetween(storeId,startDate,endDate);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

    //"yyyy-mm-dd" 형식 date 기간별 일별정산내역 조회
    //"2023-05-09","2023-05-11"  받으면 해당 기간별 일별정산내역 조회
    public List<SettlementDay> selectByDayRange(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<SettlementDay> list = settlementDayRepository.findBySettlementDateBetween(start,end);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

    //"yyyy-mm-dd" 형식 date, storeId 기간별 일별정산내역 조회
    //"2023-05-09","2023-05-11",1L 받으면 해당 기간별 일별정산내역 조회
    public List<SettlementDay> selectByDayAndStoreIdRange(String startDate, String endDate, Long storeId) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<SettlementDay> list = settlementDayRepository.findByStoreIdAndSettlementDateBetween(storeId,start,end);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return Collections.emptyList();
        }
    }

}
