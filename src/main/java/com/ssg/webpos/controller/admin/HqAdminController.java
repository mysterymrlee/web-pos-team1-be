package com.ssg.webpos.controller.admin;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.dto.StockReportDTO;
import com.ssg.webpos.dto.StoreListDTO;
import com.ssg.webpos.dto.hqMain.AllAndStoreDTO;
import com.ssg.webpos.dto.hqMain.SettlementByTermDTO;
import com.ssg.webpos.dto.hqMain.SettlementByTermListDTO;
import com.ssg.webpos.dto.hqMain.StoreDTO;
import com.ssg.webpos.dto.settlement.*;
import com.ssg.webpos.dto.stock.AllStockReportResponseDTO;
import com.ssg.webpos.dto.stock.StoreIdStockReportResponseDTO;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import com.ssg.webpos.service.SettlementDayService;
import com.ssg.webpos.service.SettlementMonthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.annotations.Nullability;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final SettlementDayRepository settlementDayRepository;
    private final OrderRepository orderRepository;
    // 스크린 첫 화면에 보이는 데이터 내역
    // 전체, 백화점 이름 + 해당 백화점의 어제 settlement_price
    // 디폴트 화면은
    @GetMapping("/main")
    public ResponseEntity main() {
        LocalDate today = LocalDate.now(); // 오늘
        LocalDate yesterday = today.minusDays(1); // 어제
        System.out.println(yesterday);
        try {
            List<SettlementDay> settlementDays = settlementDayRepository.findBySettlementDate(yesterday);
            List<StoreDTO> storeDTOs = new ArrayList<>();
            AllAndStoreDTO allAndStoreDTO = new AllAndStoreDTO();
            int totalPrice = 0;
            for (SettlementDay settlementDay : settlementDays) {
                StoreDTO storeDTO = new StoreDTO();
                totalPrice += settlementDay.getSettlementPrice(); // 시간 입력 확인, 네트워크 속도, 일정 내 기능 구현 우선이라서
                Store store = settlementDay.getStore();
                String storeName = store.getName();
                storeDTO.setStoreName(storeName);
                storeDTO.setStoreSettlementDayPrice(settlementDay.getSettlementPrice());
                storeDTOs.add(storeDTO);
            }
            allAndStoreDTO.setAllYesterdaySettlementDayPrice(totalPrice);
            allAndStoreDTO.setStoreDTO(storeDTOs);
            return new ResponseEntity(allAndStoreDTO,HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/term/storeId={storeId}")
    public ResponseEntity settlement4Term(@PathVariable(name = "storeId") int storeId ) {
        SettlementByTermListDTO settlementByTermListDTO = new SettlementByTermListDTO();
        SettlementByTermListDTO settlementByTermListDTOByStoreId = new SettlementByTermListDTO();
        LocalDate today = LocalDate.now(); // 오늘
        LocalDate yesterday = today.minusDays(1); // 어제
        LocalDate oneWeekAgo = today.minusWeeks(1); // 어제의 일주일 전
        LocalDate firstDayOfPreviousMonth = yesterday.withDayOfMonth(1); // 어제가 해당된 월의 1일
        LocalDate firstDayOfThisYear = yesterday.withDayOfYear(1); // 어제가 해당된 연도의 1일

        try {
            if(storeId == 0) {
                // 어제
                // 주문수, 매출액, 수수료, 영업이익, 시작날짜, 종료 날짜
                SettlementByTermDTO yesterdayDTO = new SettlementByTermDTO();
                yesterdayDTO.setOrderCount(orderRepository.countOrdersByYesterday());
                yesterdayDTO.setSettlementPrice(settlementDayRepository.sumOfAllSettlementPrice());
                yesterdayDTO.setCharge(settlementDayRepository.sumOfAllCharge());
                yesterdayDTO.setProfit(settlementDayRepository.sumOfAllProfit());
                yesterdayDTO.setStartDate(yesterday);
                LocalDate endDate = null;
                yesterdayDTO.setEndDate(endDate);
                settlementByTermListDTO.setYesterdayDTO(yesterdayDTO);
                // 이번주
                SettlementByTermDTO thisWeekDTO = new SettlementByTermDTO();
                thisWeekDTO.setOrderCount(orderRepository.countOrderByThisWeek());
                thisWeekDTO.setSettlementPrice(settlementDayRepository.sumOfThisWeekAllSettlementPrice());
                thisWeekDTO.setCharge(settlementDayRepository.sumOfThisWeekAllSettlemetCharge());
                thisWeekDTO.setProfit(settlementDayRepository.sumOfThisWeekAllSettlemetProfit());
                thisWeekDTO.setStartDate(oneWeekAgo);
                thisWeekDTO.setEndDate(yesterday);
                settlementByTermListDTO.setThisWeekDTO(thisWeekDTO);
                // 이번달
                SettlementByTermDTO thisMonthDTO = new SettlementByTermDTO();
                thisMonthDTO.setOrderCount(orderRepository.countOrderByThisMonth());
                thisMonthDTO.setSettlementPrice(settlementDayRepository.sumOfThisMonthSettlementPrice());
                thisMonthDTO.setCharge(settlementDayRepository.sumOfThisMonthCharge());
                thisMonthDTO.setProfit(settlementDayRepository.sumOfThisMonthProfit());
                thisMonthDTO.setStartDate(firstDayOfPreviousMonth);
                thisMonthDTO.setEndDate(yesterday);
                settlementByTermListDTO.setThisMonthDTO(thisMonthDTO);
                // 올해
                SettlementByTermDTO thisYearDTO = new SettlementByTermDTO();
                thisYearDTO.setOrderCount(orderRepository.countOrderByThisYear());
                thisYearDTO.setSettlementPrice(orderRepository.sumOfAllSettlementPrice());
                thisYearDTO.setCharge(orderRepository.sumOfAllCharge());
                thisYearDTO.setProfit(orderRepository.sumOfAllProfit());
                thisYearDTO.setStartDate(firstDayOfThisYear);
                thisYearDTO.setEndDate(yesterday);
                settlementByTermListDTO.setThisYearDTO(thisYearDTO);
                return new ResponseEntity<>(settlementByTermListDTO, HttpStatus.OK);
            } else {
                // 어제
                // 주문수, 매출액, 수수료, 영업이익, 시작날짜, 종료 날짜
                SettlementByTermDTO yesterdayDTOByStoreId = new SettlementByTermDTO();
                yesterdayDTOByStoreId.setOrderCount(orderRepository.countOrdersByYesterdayAndStoreID(storeId));
                yesterdayDTOByStoreId.setSettlementPrice(settlementDayRepository.settlementDaySettlementPrice(storeId));
                yesterdayDTOByStoreId.setCharge(settlementDayRepository.settlementDayCharge(storeId));
                yesterdayDTOByStoreId.setProfit(settlementDayRepository.settlementDayProfit(storeId));
                yesterdayDTOByStoreId.setStartDate(yesterday);
                LocalDate endDate = null;
                yesterdayDTOByStoreId.setEndDate(endDate);
                settlementByTermListDTOByStoreId.setYesterdayDTO(yesterdayDTOByStoreId);
                // 이번주
                SettlementByTermDTO thisWeekDTOByStoreId = new SettlementByTermDTO();
                thisWeekDTOByStoreId.setOrderCount(orderRepository.countOrderByThisWeekAndStoreId(storeId));
                thisWeekDTOByStoreId.setSettlementPrice(settlementDayRepository.sumOfThisWeekAllSettlementPriceByStoreId(storeId));
                thisWeekDTOByStoreId.setCharge(settlementDayRepository.sumOfThisWeekAllSettlemetChargeByStoreId(storeId));
                thisWeekDTOByStoreId.setProfit(settlementDayRepository.sumOfThisWeekAllSettlemetProfitByStoreId(storeId));
                thisWeekDTOByStoreId.setStartDate(oneWeekAgo);
                thisWeekDTOByStoreId.setEndDate(yesterday);
                settlementByTermListDTOByStoreId.setThisWeekDTO(thisWeekDTOByStoreId);
                // 이번달
                SettlementByTermDTO thisMonthDTOByStoreId = new SettlementByTermDTO();
                thisMonthDTOByStoreId.setOrderCount(orderRepository.countOrderByThisMonthByStoreId(storeId));
                thisMonthDTOByStoreId.setSettlementPrice(settlementDayRepository.sumOfThisMonthSettlementPriceAndStoreId(storeId));
                thisMonthDTOByStoreId.setCharge(settlementDayRepository.sumOfThisMonthChargeAndStoreId(storeId));
                thisMonthDTOByStoreId.setProfit(settlementDayRepository.sumOfThisMonthProfitAndStoreId(storeId));
                thisMonthDTOByStoreId.setStartDate(firstDayOfPreviousMonth);
                thisMonthDTOByStoreId.setEndDate(yesterday);
                settlementByTermListDTOByStoreId.setThisMonthDTO(thisMonthDTOByStoreId);
                // 올해
                SettlementByTermDTO thisYearDTOByStoreId = new SettlementByTermDTO();
                thisYearDTOByStoreId.setOrderCount(orderRepository.countOrderByThisYearAndStoreId(storeId));
                thisYearDTOByStoreId.setSettlementPrice(orderRepository.sumOfAllSettlementPriceByStoreId(storeId));
                thisYearDTOByStoreId.setCharge(orderRepository.sumOfAllChargeByStoreId(storeId));
                thisYearDTOByStoreId.setProfit(orderRepository.sumOfAllProfitByStoreId(storeId));
                thisYearDTOByStoreId.setStartDate(firstDayOfThisYear);
                thisYearDTOByStoreId.setEndDate(yesterday);
                settlementByTermListDTOByStoreId.setThisYearDTO(thisYearDTOByStoreId);
                return new ResponseEntity<>(settlementByTermListDTOByStoreId, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

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
            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
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
            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
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

            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
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

            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
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
            return new ResponseEntity<>(reportDTOs,HttpStatus.OK);
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
            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
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
            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
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
                stockReportDTO.setProductName(stockReport.getProduct().getName()); // 같은 이름을 여러개 가진 상품이 있다면?
                stockReportDTO.setCurrentStock(stockReport.getProduct().getStock());
                stockReportDTO.setSalePrice(stockReport.getProduct().getSalePrice());
                stockReportDTOs.add(stockReportDTO);
            }

            return new ResponseEntity<>(stockReportDTOs, HttpStatus.OK);
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
            return new ResponseEntity<>(allStockReportResponseDTOs, HttpStatus.OK);
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
            return new ResponseEntity<>(allStockReportResponseDTOs, HttpStatus.OK);
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
            return new ResponseEntity<>(storeListDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // 전체 store_id 재고내역 조회(store_report 테이블 조회)
    @GetMapping("/stock-report-view")
    @Transactional
    public ResponseEntity stockReportAll() {
        try {
            List<StockReport> stockReports = stockReportRepository.findAll();
            List<StoreIdStockReportResponseDTO> lists = new ArrayList<>();
            // storeId로 받은 여러개의 stockReport
            for (StockReport stockReport : stockReports) {
                StoreIdStockReportResponseDTO DTO = new StoreIdStockReportResponseDTO();
                DTO.setCurrentStock(stockReport.getCurrentStock());
                DTO.setSubmit(stockReport.isSubmit()); // boolean은 get이 아닌 is 그대로 가져간다.
                Product product = stockReport.getProduct();
                DTO.setProductName(product.getName());
                DTO.setProductSalePrice(product.getSalePrice());
                DTO.setCategory(product.getCategory());
                lists.add(DTO);
            }
            return new ResponseEntity<>(lists,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // 재고 생성날짜별 전체 조회
    @GetMapping("/stock-report-view/{createdDate}")
    @Transactional
    public ResponseEntity stockReportAllByCreatedDate(@PathVariable("createdDate") String createdDate) {
        try {
            LocalDateTime date = LocalDateTime.parse(createdDate);
            List<StockReport> stockReports = stockReportRepository.findByCreatedDate(date);
            List<StoreIdStockReportResponseDTO> lists = new ArrayList<>();
            // storeId로 받은 여러개의 stockReport
            for (StockReport stockReport : stockReports) {
                StoreIdStockReportResponseDTO DTO = new StoreIdStockReportResponseDTO();
                DTO.setCurrentStock(stockReport.getCurrentStock());
                DTO.setSubmit(stockReport.isSubmit()); // boolean은 get이 아닌 is 그대로 가져간다.
                Product product = stockReport.getProduct();
                DTO.setProductName(product.getName());
                DTO.setProductSalePrice(product.getSalePrice());
                DTO.setCategory(product.getCategory());
                lists.add(DTO);
            }
            return new ResponseEntity<>(lists,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // 재고 생성날짜 기간별 전체 조회
    @GetMapping("/stock-report-view/{startDate}/{endDate}")
    @Transactional
    public ResponseEntity stockReportAllByCreatedDateBetween(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        try {
            LocalDateTime end = LocalDateTime.parse(endDate);
            LocalDateTime start = LocalDateTime.parse(startDate);
            List<StockReport> stockReports = stockReportRepository.findByCreatedDateBetween(start,end);
            List<StoreIdStockReportResponseDTO> lists = new ArrayList<>();
            // storeId로 받은 여러개의 stockReport
            for (StockReport stockReport : stockReports) {
                StoreIdStockReportResponseDTO DTO = new StoreIdStockReportResponseDTO();
                DTO.setCurrentStock(stockReport.getCurrentStock());
                DTO.setSubmit(stockReport.isSubmit()); // boolean은 get이 아닌 is 그대로 가져간다.
                Product product = stockReport.getProduct();
                DTO.setProductName(product.getName());
                DTO.setProductSalePrice(product.getSalePrice());
                DTO.setCategory(product.getCategory());
                lists.add(DTO);
            }
            return new ResponseEntity<>(lists,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }


}
