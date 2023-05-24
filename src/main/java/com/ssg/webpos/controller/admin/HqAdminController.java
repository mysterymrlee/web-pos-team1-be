package com.ssg.webpos.controller.admin;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.dto.StockReportDTO;
import com.ssg.webpos.dto.StoreListDTO;
import com.ssg.webpos.dto.settlement.*;
import com.ssg.webpos.dto.stock.AllStockReportResponseDTO;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import com.ssg.webpos.service.SettlementDayService;
import com.ssg.webpos.service.SettlementMonthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hq")
@Slf4j
@RequiredArgsConstructor
public class HqAdminController {
    // hq 기능 : 재고 조회, 수정, 삭제, 재고 리포트(주말 재고 현황) 제출
    //          정산 전체 조회,
    private final SettlementDayService settlementDayService;
    private final SettlementMonthService settlementMonthService;
    private final StockReportRepository stockReportRepository;
    private final StoreRepository storeRepository;

    // 전체 월별정산내역 조회
    // "2023"을 받으면 2023년에 생성된 전체 월별정산내역 조회
    @PostMapping("/settlement-month/all-store")
    public ResponseEntity settlementMonth(@RequestBody RequsestSettlementMonthDTO requestSettlementMonthDTO) {
        try {
            String year = requestSettlementMonthDTO.getYear();
            List<SettlementMonth> settlementMonths = settlementMonthService.selectByYear(year);
            List<SettlementMonthReportDTO> reportDTOs = new ArrayList<>();

            for(SettlementMonth settlementMonth:settlementMonths) {
                SettlementMonthReportDTO reportDTO = new SettlementMonthReportDTO();
                reportDTO.setSettlementMontnId(settlementMonth.getId());
                reportDTO.setSettlementPrice(settlementMonth.getSettlementPrice());
                reportDTO.setSettlementDate(settlementMonth.getSettlementDate());
                reportDTO.setStoreId(settlementMonth.getStore().getId());
                reportDTO.setStoreName(settlementMonth.getStore().getName());
                reportDTO.setCreatedDate(settlementMonth.getCreatedDate());
                reportDTOs.add(reportDTO);
            }
            return new ResponseEntity(reportDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.getStackTrace();
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // store_id별 월별정산내역 조회
    // 1L, "2023"을 받으면 store_id=1L인 가게의 2023년 발생 월별정산내역 조회
    @PostMapping("/settlement-month/store-id")
    public ResponseEntity settlementMonthByStoreId(@RequestBody RequestSettlementMonthByStoreIdDTO requestSettlementMonthByStoreIdDTO) {
        try {
            String year = requestSettlementMonthByStoreIdDTO.getYear();
            Long storeId = requestSettlementMonthByStoreIdDTO.getStoreId();
            List<SettlementMonth> settlementMonths = settlementMonthService.selectByStoreIdAndDayBetween(storeId, year);
            List<SettlementMonthReportDTO> reportDTOs = new ArrayList<>();

            for(SettlementMonth settlementMonth:settlementMonths) {
                SettlementMonthReportDTO reportDTO = new SettlementMonthReportDTO();
                reportDTO.setSettlementMontnId(settlementMonth.getId());
                reportDTO.setSettlementPrice(settlementMonth.getSettlementPrice());
                reportDTO.setSettlementDate(settlementMonth.getSettlementDate());
                reportDTO.setStoreId(settlementMonth.getStore().getId());
                reportDTO.setStoreName(settlementMonth.getStore().getName());
                reportDTO.setCreatedDate(settlementMonth.getCreatedDate());
                reportDTOs.add(reportDTO);
            }
            return new ResponseEntity(reportDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // 전체 기간별 월별정산내역 조회
    // "2023-02","2023-03" 받으면 2023-02부터 2023-03의 전체 월별정산내역 조회
    @PostMapping("/settlement-month/range/all-store")
    public ResponseEntity settlementMonthRange(@RequestBody RequestSettlementMonthRangeDTO requestSettlementMonthRangeDTO) {
        try {
            String StartDate = requestSettlementMonthRangeDTO.getStartDate();
            String EndDate = requestSettlementMonthRangeDTO.getEndDate();
            List<SettlementMonth> settlementMonths = settlementMonthService.selectByMonthRange(StartDate,EndDate);
            List<SettlementMonthReportDTO> reportDTOs = new ArrayList<>();

            for(SettlementMonth settlementMonth:settlementMonths) {
                SettlementMonthReportDTO reportDTO = new SettlementMonthReportDTO();
                reportDTO.setSettlementMontnId(settlementMonth.getId());
                reportDTO.setSettlementPrice(settlementMonth.getSettlementPrice());
                reportDTO.setSettlementDate(settlementMonth.getSettlementDate());
                reportDTO.setStoreId(settlementMonth.getStore().getId());
                reportDTO.setStoreName(settlementMonth.getStore().getName());
                reportDTO.setCreatedDate(settlementMonth.getCreatedDate());
                reportDTOs.add(reportDTO);
            }

            return new ResponseEntity(reportDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // store_id별 기간별 월별정산내역 조회
    // 1L, "2023-02","2023-03" 받으면 store_id=1L이고 2023-02부터 2023-03의 월별정산내역 조회
    @PostMapping("/settlement-month/range/store_id")
    public ResponseEntity settlementMonthRangeByStoreId(@RequestBody RequestSettlementMonthRangeByStoreIdDTO requestSettlementMonthRangeByStoreIdDTO) {
        try {
            String StartDate = requestSettlementMonthRangeByStoreIdDTO.getStartDate();
            String EndDate = requestSettlementMonthRangeByStoreIdDTO.getEndDate();
            Long storeId = requestSettlementMonthRangeByStoreIdDTO.getStoreId();
            List<SettlementMonth> settlementMonths = settlementMonthService.selectByStoreIdAndMonthRange(storeId,StartDate,EndDate);
            List<SettlementMonthReportDTO> reportDTOs = new ArrayList<>();

            for(SettlementMonth settlementMonth:settlementMonths) {
                SettlementMonthReportDTO reportDTO = new SettlementMonthReportDTO();
                reportDTO.setSettlementMontnId(settlementMonth.getId());
                reportDTO.setSettlementPrice(settlementMonth.getSettlementPrice());
                reportDTO.setSettlementDate(settlementMonth.getSettlementDate());
                reportDTO.setStoreId(settlementMonth.getStore().getId());
                reportDTO.setStoreName(settlementMonth.getStore().getName());
                reportDTO.setCreatedDate(settlementMonth.getCreatedDate());
                reportDTOs.add(reportDTO);
            }

            return new ResponseEntity(reportDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // 전체 일별정산내역 조회
    // "2023-05"을 받으면 2023년 5월에 생성된 전체 일별정산내역 조회
    @PostMapping("/settlement-day/all-store")
    public ResponseEntity settlementDay(@RequestBody RequestSettlementDayDTO requestSettlementDayDTO) {
        try {
            String date = requestSettlementDayDTO.getDate();
            List<SettlementDay> settlementDays = settlementDayService.selectByYearMonth(date);
            List<SettlementDayReportDTO> reportDTOs = new ArrayList<>();

            for (SettlementDay settlementDay:settlementDays) {
                SettlementDayReportDTO reportDTO = new SettlementDayReportDTO();
                reportDTO.setSettlementDayId(settlementDay.getId());
                reportDTO.setSettlementPrice(settlementDay.getSettlementPrice());
                reportDTO.setSettlementDate(settlementDay.getSettlementDate());
                reportDTO.setStoreId(settlementDay.getStore().getId());
                reportDTO.setStoreName(settlementDay.getStore().getName());
                reportDTO.setCreatedDate(settlementDay.getCreatedDate());
                reportDTOs.add(reportDTO);
            }
            return new ResponseEntity(reportDTOs,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // store_id별 기간별 일별정산내역 조회
    // "2023-05-08","2023-05-10",1 을 받으면 해당 기간, store_id 생성된 전체 일별정산내역 조회
    @PostMapping("/settlement-day/range/store-id")
    public ResponseEntity settlementDayByStoreId(@RequestBody RequestSettlementDayRangeByStoreIdDTO requestSettlementDayRangeByStoreIdDTO) {
        try {
            String startDate = requestSettlementDayRangeByStoreIdDTO.getStartDate();
            String endDate = requestSettlementDayRangeByStoreIdDTO.getEndDate();
            Long storeId = requestSettlementDayRangeByStoreIdDTO.getStoreId();
            List<SettlementDay> settlementDays = settlementDayService.selectByDayAndStoreIdRange(startDate,endDate,storeId);
            List<SettlementDayReportDTO> reportDTOs = new ArrayList<>();

            for (SettlementDay settlementDay:settlementDays) {
                SettlementDayReportDTO reportDTO = new SettlementDayReportDTO();
                reportDTO.setSettlementDayId(settlementDay.getId());
                reportDTO.setSettlementPrice(settlementDay.getSettlementPrice());
                reportDTO.setSettlementDate(settlementDay.getSettlementDate());
                reportDTO.setStoreId(settlementDay.getStore().getId());
                reportDTO.setStoreName(settlementDay.getStore().getName());
                reportDTO.setCreatedDate(settlementDay.getCreatedDate());
                reportDTOs.add(reportDTO);
            }
            return new ResponseEntity(reportDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // 전체 기간별 일별정산내역 조회
    // "2023-05-09","2023-05-11"을 받으면 해당 기간에 생성된 일별정산내역 조회
    @PostMapping("/settlement-day/range/all-store")
    public ResponseEntity settlementDayRange(@RequestBody RequestSettlementDayRangeDTO requestSettlementDayRangeDTO) {
        try {
            String startDate = requestSettlementDayRangeDTO.getStartDate();
            String endDate = requestSettlementDayRangeDTO.getEndDate();
            List<SettlementDay> settlementDays = settlementDayService.selectByDayRange(startDate,endDate);
            List<SettlementDayReportDTO> reportDTOs = new ArrayList<>();

            for (SettlementDay settlementDay:settlementDays) {
                SettlementDayReportDTO reportDTO = new SettlementDayReportDTO();
                reportDTO.setSettlementDayId(settlementDay.getId());
                reportDTO.setSettlementPrice(settlementDay.getSettlementPrice());
                reportDTO.setSettlementDate(settlementDay.getSettlementDate());
                reportDTO.setStoreId(settlementDay.getStore().getId());
                reportDTO.setStoreName(settlementDay.getStore().getName());
                reportDTO.setCreatedDate(settlementDay.getCreatedDate());
                reportDTOs.add(reportDTO);
            }
            return new ResponseEntity(reportDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }


    // 전체 재고 조회
    @GetMapping("/stock/store")
    public ResponseEntity stockReport() {
        try {
            List<StockReportDTO> stockReportDTOs = new ArrayList<>();
            List<StockReport> stockReports = stockReportRepository.findAll();

            for (StockReport stockReport : stockReports) {
                StockReportDTO stockReportDTO = new StockReportDTO();
                stockReportDTO.setStoreName(stockReport.getStore().getName());
                stockReportDTO.setProductName(stockReport.getProduct().getName());
                stockReportDTO.setCurrentStock(stockReport.getProduct().getStock());
                stockReportDTO.setSalePrice(stockReport.getProduct().getSalePrice());
                stockReportDTOs.add(stockReportDTO);
            }

            return new ResponseEntity(stockReportDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }



    // 발주 신청 전체 내역 조회
    // service 활용해서 is_submit=1인 열들만 조회
    // hq는 manager, staff가 발주 신청한 내역만 조회가능하다.List<AllStockReportResponseDTO>
    @GetMapping("/ordered-product-list")
    public ResponseEntity submitStockReport() {
        try {
            List<StockReport> stockReports = stockReportRepository.findByIsSubmit(true);
            List<AllStockReportResponseDTO> allStockReportResponseDTOs = new ArrayList<>();

            for (StockReport stockReport : stockReports) {
                AllStockReportResponseDTO allStockReportResponseDTO = new AllStockReportResponseDTO();
                allStockReportResponseDTO.setLastModifiedDate(stockReport.getLastModifiedDate());
                allStockReportResponseDTO.setProductName(stockReport.getProduct().getName());
                allStockReportResponseDTO.setProductCategory(stockReport.getProduct().getCategory());
                allStockReportResponseDTO.setProductSalePrice(stockReport.getProduct().getSalePrice());
                allStockReportResponseDTO.setStoreName(stockReport.getStore().getName());
                allStockReportResponseDTO.setCurrentStock(stockReport.getProduct().getStock());
                allStockReportResponseDTOs.add(allStockReportResponseDTO);
            }
            return new ResponseEntity(allStockReportResponseDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
    // 발주 신청 store_id별 내역 조회
    // service 활용해서 is_submit=1이고 특정 store_id를 가진 열들만 조회
    // hq는 manager, staff가 발주 신청한 내역만 조회가능하다.List<AllStockReportResponseDTO>
    @GetMapping("/ordered-product-list/store-id")
    public ResponseEntity submitStockReportByStoreId(@RequestParam("storeId") Long storeId) {
        try {
            List<StockReport> stockReports = stockReportRepository.findByIsSubmitAndStoreId(true,storeId);
            List<AllStockReportResponseDTO> allStockReportResponseDTOs = new ArrayList<>();

            for(StockReport stockReport:stockReports) {
                AllStockReportResponseDTO allStockReportResponseDTO = new AllStockReportResponseDTO();
                allStockReportResponseDTO.setLastModifiedDate(stockReport.getLastModifiedDate());
                allStockReportResponseDTO.setProductName(stockReport.getProduct().getName());
                allStockReportResponseDTO.setProductCategory(stockReport.getProduct().getCategory());
                allStockReportResponseDTO.setProductSalePrice(stockReport.getProduct().getSalePrice());
                allStockReportResponseDTO.setStoreName(stockReport.getStore().getName());
                allStockReportResponseDTO.setCurrentStock(stockReport.getProduct().getStock());
                allStockReportResponseDTOs.add(allStockReportResponseDTO);
            }
            return new ResponseEntity(allStockReportResponseDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }



    // 로그인 시 보이는 화면
    // 매장별 정보 DTO // httpstatus, body 넣어서. VO
    @GetMapping("/main/store-list")
    public ResponseEntity storeList() {
        try {
            List<Store> stores = storeRepository.findAll();
            List<StoreListDTO> storeListDTOs = new ArrayList<>();

            for (Store store : stores) {
                StoreListDTO storeListDTO = new StoreListDTO();
                storeListDTO.setName(store.getName());
                storeListDTO.setDescription(store.getDescription());
                storeListDTO.setTelNumber(store.getTelNumber());
                storeListDTO.setAddress(store.getAddress());
                storeListDTO.setImageUrl(store.getImageUrl());
                storeListDTOs.add(storeListDTO);
            }
            return new ResponseEntity(storeListDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }


}
