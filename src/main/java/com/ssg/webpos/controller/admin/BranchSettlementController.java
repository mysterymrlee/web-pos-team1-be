package com.ssg.webpos.controller.admin;

import com.ssg.webpos.domain.BranchAdmin;
import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.dto.SettlementDayReportDTO;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import com.ssg.webpos.service.SettlementDayService;
import com.ssg.webpos.service.SettlementMonthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/manager")
@Slf4j
@RequiredArgsConstructor
public class BranchSettlementController {
    private final SettlementDayRepository settlementDayRepository;
    private final SettlementMonthRepository settlementMonthRepository;
    private final SettlementDayService settlementDayService;
    private final SettlementMonthService settlementMonthService;

    // settlement_day 전체 내역 조회 GET
    @GetMapping("/settlement-day")
    public List<SettlementDayReportDTO> getSettlementDayReport() {
        List<SettlementDay> settlementDays = settlementDayRepository.findAll();
        List<SettlementDayReportDTO> reportDTOs = new ArrayList<>();

        for (SettlementDay settlementDay : settlementDays) {
            SettlementDayReportDTO reportDTO = new SettlementDayReportDTO();
            reportDTO.setSettlementDayId(settlementDay.getId());
            reportDTO.setSettlementPrice(settlementDay.getSettlementPrice());
            reportDTO.setSettlementDate(settlementDay.getSettlementDate());
            reportDTO.setStoreId(settlementDay.getStore().getId());
            reportDTO.setCreatedDate(settlementDay.getCreatedDate());
            reportDTOs.add(reportDTO);
        }

        return reportDTOs;
    }
    //조건을 명시한 상태에서 settlement_day 내역 GET (조건 : store_id=1L, settlement_date="2023-05-08")
    @GetMapping("/test1")
    public List<SettlementDayReportDTO> test1() { //점장 로그인 기능 구현시 팀장님 코드 활용
//        팀장님 작성 BranchAdmin branchAdmin = principalDetail.getUser(); 이거 활용해서 로그인한 사람의 소속 가게 store_id불러올 수 있다.
//        팀장님 작성 Long storeId = branchAdmin.getStore().getId();
//        지금 해야하는 것 : 요청 받은 날짜의 결제 내역을 나오게 하는 것
        List<SettlementDay> settlementDays = settlementDayService.selectByStoreIdAndDay(1L,"2023-05-08");
        List<SettlementDayReportDTO> reportDTOs = new ArrayList<>();

        for (SettlementDay settlementDay : settlementDays) {
            SettlementDayReportDTO reportDTO = new SettlementDayReportDTO();
            reportDTO.setSettlementDayId(settlementDay.getId());
            reportDTO.setSettlementPrice(settlementDay.getSettlementPrice());
            reportDTO.setSettlementDate(settlementDay.getSettlementDate());
            reportDTO.setStoreId(settlementDay.getStore().getId());
            reportDTO.setCreatedDate(settlementDay.getCreatedDate());
            reportDTOs.add(reportDTO);
        }

        return reportDTOs;
    }

    @GetMapping("/test2")
    public List<SettlementDayReportDTO> test2(@RequestParam("settlementDate")String SettlementDate) throws DateTimeParseException {
        // 20022-05-08같이 테이블에 없는 날짜 내역이 올 경우 에러 -> DateTimeParseException
        try {
            List<SettlementDay> settlementDays = settlementDayService.selectByStoreIdAndDay(1L,SettlementDate);
            List<SettlementDayReportDTO> reportDTOs = new ArrayList<>();

            for (SettlementDay settlementDay : settlementDays) {
                SettlementDayReportDTO reportDTO = new SettlementDayReportDTO();
                reportDTO.setSettlementDayId(settlementDay.getId());
                reportDTO.setSettlementPrice(settlementDay.getSettlementPrice());
                reportDTO.setSettlementDate(settlementDay.getSettlementDate());
                reportDTO.setStoreId(settlementDay.getStore().getId());
                reportDTO.setCreatedDate(settlementDay.getCreatedDate());
                reportDTOs.add(reportDTO);
            }

            return reportDTOs;
        } catch (Exception e) {
            return Collections.emptyList();
        }

    }

//     코드 시작
//    @PostMapping("/settlement-day")
//    public ResponseEntity<SettlementDayResponseDTO> getSettlementDayByDate (
//            // @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//            @RequestBody SettlementDayRequestDTO requestDTO) {
//        String dateStr = requestDTO.getDate();
//        System.out.println("dateStr = " + dateStr);
//        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
//        System.out.println(date);
//        try {
//            List<SettlementDay> settlementDayListByDay = settlementDayRepository.findBySettlementDate(date);
//            for (SettlementDay settlementDay : settlementDayListByDay) {
//                System.out.println("settlementDay = " + settlementDay);
//            }
//            SettlementDayResponseDTO responseDTO = new SettlementDayResponseDTO(settlementDayListByDay);
//            return new ResponseEntity(responseDTO ,HttpStatus.OK);
//        } catch (Exception e) {
//            log.error("Error occurred", e);
//            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//     팀장님 코드 종료




//    public ResponseEntity<List<SettlementDay>> GetSettlementDayByDate() throws Exception {
//        LocalDate localDate = LocalDate.parse("2023-05-08");
//        List<SettlementDay> settlementDayListByDay = settlementDayRepository.findBySettlementDate(localDate);
//        return new ResponseEntity(settlementDayListByDay, HttpStatus.OK);
//    }

    // 점장의 정산 내역 조회 페이지 (월별 조회 페이지, 디폴트 : 2023.04 정산내역 표시)
    // 점장이 HQ에게 일별 정산 내역 제출
    // 점장이 HQ에게 월별 정산 내역 제출


}
