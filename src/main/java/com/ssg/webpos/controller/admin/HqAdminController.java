package com.ssg.webpos.controller.admin;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.dto.StockReportDTO;
import com.ssg.webpos.dto.StoreListDTO;
import com.ssg.webpos.dto.hqMain.AllAndStoreDTO;
import com.ssg.webpos.dto.hqMain.SettlementByTermDTO;
import com.ssg.webpos.dto.hqMain.SettlementByTermListDTO;
import com.ssg.webpos.dto.hqMain.StoreDTO;
import com.ssg.webpos.dto.hqStock.StockReportResponseDTO;
import com.ssg.webpos.dto.settlement.*;
import com.ssg.webpos.dto.stock.AllStockReportResponseDTO;
import com.ssg.webpos.dto.stock.StoreIdStockReportResponseDTO;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import com.ssg.webpos.service.SettlementDayService;
import com.ssg.webpos.service.SettlementMonthService;
import com.ssg.webpos.service.hqController.method.HqControllerStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final SettlementDayRepository settlementDayRepository;
    private final OrderRepository orderRepository;
    private final HqControllerStockService hqControllerStockService;
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

        LocalDate todayOneWeekago = today.minusWeeks(1); // 오늘의 일주일 전 날짜

        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY; // 주의 처음 날짜를 원하는 요일로 설정 (예: 월요일)
        LocalDate firstDayOfLastWeek = yesterday.with(DayOfWeek.MONDAY); // 어제의 날짜에서 해당하는 주의 월요일 계산
        // 어제의 일주일 전 날짜 계산
        LocalDate yesterdayOnneWeekAgo = yesterday.minusWeeks(1);

        LocalDate firstDayOfPreviousMonth = yesterday.withDayOfMonth(1); // 어제가 해당된 월의 1일
        LocalDate firstDayOfThisYear = yesterday.withDayOfYear(1); // 어제가 해당된 연도의 1일

        try {
            if(storeId == 0) {
                // 어제
                // 주문수, 매출액, 수수료, 영업이익, 시작날짜, 종료 날짜
                SettlementByTermDTO yesterdayDTO = new SettlementByTermDTO();
                yesterdayDTO.setOrderCount(orderRepository.countOrdersByYesterday()); // 어제의 주문수
                yesterdayDTO.setSettlementPrice(settlementDayRepository.sumOfAllSettlementPrice()); // 어제의 매출액합
                yesterdayDTO.setCharge(settlementDayRepository.sumOfAllCharge()); // 어제의 수수료합
                yesterdayDTO.setProfit(settlementDayRepository.sumOfAllProfit()); // 어제의 영업이익합
                yesterdayDTO.setStartDate(yesterday); // 어제
                LocalDate endDate = null;
                yesterdayDTO.setEndDate(endDate);
                settlementByTermListDTO.setYesterdayDTO(yesterdayDTO);
                // 이번주
                SettlementByTermDTO thisWeekDTO = new SettlementByTermDTO();
                thisWeekDTO.setOrderCount(orderRepository.countOrderByThisWeekBeweenYesterdayAndYesterday1WeekAgo()); // 어제의 일주일 전부터 어제의 주문수
                thisWeekDTO.setSettlementPrice(orderRepository.sumOfFinalOrderPriceBetweenYesterday1WeekAgoAndYesterday()); // 어제의 일주일 전부터 어제의 매출액 합
                thisWeekDTO.setCharge(orderRepository.sumOfChargeBetweenYesterday1WeekAgoAndYesterday()); // 어제의 일주일 전부터 어제의 수수료 합
                thisWeekDTO.setProfit(orderRepository.sumOfProfitBetweenYesterday1WeekAgoAndYesterday()); // 어제의 일주일 전부터 어제의 영업이익합
                thisWeekDTO.setStartDate(yesterdayOnneWeekAgo); // 어제의 일주일 전
                thisWeekDTO.setEndDate(yesterday); //어제
                settlementByTermListDTO.setThisWeekDTO(thisWeekDTO);
                // 이번달
                SettlementByTermDTO thisMonthDTO = new SettlementByTermDTO();
                thisMonthDTO.setOrderCount(orderRepository.countOrderByThisMonth());
                thisMonthDTO.setSettlementPrice(settlementDayRepository.sumOfThisMonthSettlementPrice());
                thisMonthDTO.setCharge(settlementDayRepository.sumOfThisMonthCharge());
                thisMonthDTO.setProfit(settlementDayRepository.sumOfThisMonthProfit());
                thisMonthDTO.setStartDate(firstDayOfPreviousMonth); // 어제가 해당하는 월의 1일
                thisMonthDTO.setEndDate(yesterday); // 어제
                settlementByTermListDTO.setThisMonthDTO(thisMonthDTO);
                // 올해
                SettlementByTermDTO thisYearDTO = new SettlementByTermDTO();
                thisYearDTO.setOrderCount(orderRepository.countOrderByThisYear());
                thisYearDTO.setSettlementPrice(orderRepository.sumOfAllSettlementPrice());
                thisYearDTO.setCharge(orderRepository.sumOfAllCharge());
                thisYearDTO.setProfit(orderRepository.sumOfAllProfit());
                thisYearDTO.setStartDate(firstDayOfThisYear); // 어제가 해당하는 연도의 1월 1일
                thisYearDTO.setEndDate(yesterday); // 어제
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
                yesterdayDTOByStoreId.setStartDate(yesterday); // 어제
                LocalDate endDate = null;
                yesterdayDTOByStoreId.setEndDate(endDate);
                settlementByTermListDTOByStoreId.setYesterdayDTO(yesterdayDTOByStoreId);
                // 이번주
                SettlementByTermDTO thisWeekDTOByStoreId = new SettlementByTermDTO();
                thisWeekDTOByStoreId.setOrderCount(orderRepository.countOrderByThisWeekBeweenYesterdayAndYesterday1WeekAgoBystoreId(storeId));
                thisWeekDTOByStoreId.setSettlementPrice(orderRepository.sumOfFinalOrderPriceBetweenYesterday1WeekAgoAndYesterdayByStoreId(storeId));
                thisWeekDTOByStoreId.setCharge(orderRepository.sumOfChargeBetweenYesterday1WeekAgoAndYesterdayByStoreId(storeId));
                thisWeekDTOByStoreId.setProfit(orderRepository.sumOfProfitBetweenYesterday1WeekAgoAndYesterdayByStoreId(storeId));
                thisWeekDTOByStoreId.setStartDate(yesterdayOnneWeekAgo); // 어제의 일주일 전
                thisWeekDTOByStoreId.setEndDate(yesterday); // 어제
                settlementByTermListDTOByStoreId.setThisWeekDTO(thisWeekDTOByStoreId);
                // 이번달
                SettlementByTermDTO thisMonthDTOByStoreId = new SettlementByTermDTO();
                thisMonthDTOByStoreId.setOrderCount(orderRepository.countOrderByThisMonthByStoreId(storeId));
                thisMonthDTOByStoreId.setSettlementPrice(settlementDayRepository.sumOfThisMonthSettlementPriceAndStoreId(storeId));
                thisMonthDTOByStoreId.setCharge(settlementDayRepository.sumOfThisMonthChargeAndStoreId(storeId));
                thisMonthDTOByStoreId.setProfit(settlementDayRepository.sumOfThisMonthProfitAndStoreId(storeId));
                thisMonthDTOByStoreId.setStartDate(firstDayOfPreviousMonth); // 어제가 해당하는 월의 1일
                thisMonthDTOByStoreId.setEndDate(yesterday); // 어제
                settlementByTermListDTOByStoreId.setThisMonthDTO(thisMonthDTOByStoreId);
                // 올해
                SettlementByTermDTO thisYearDTOByStoreId = new SettlementByTermDTO();
                thisYearDTOByStoreId.setOrderCount(orderRepository.countOrderByThisYearAndStoreId(storeId));
                thisYearDTOByStoreId.setSettlementPrice(orderRepository.sumOfAllSettlementPriceByStoreId(storeId));
                thisYearDTOByStoreId.setCharge(orderRepository.sumOfAllChargeByStoreId(storeId));
                thisYearDTOByStoreId.setProfit(orderRepository.sumOfAllProfitByStoreId(storeId));
                thisYearDTOByStoreId.setStartDate(firstDayOfThisYear); // 어제가 해당하는 연도의 1월 1일
                thisYearDTOByStoreId.setEndDate(yesterday); // 어제
                settlementByTermListDTOByStoreId.setThisYearDTO(thisYearDTOByStoreId);
                return new ResponseEntity<>(settlementByTermListDTOByStoreId, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // 재고 관리
    // 재고 수량이 30미만인 재고들이 조회됨
    // 어제,이번주,이번달,3개월,기간별 조회, default : 전체  // 1. 전체 매출,store_id별 매출 2. 판매 여부 조회 3. 재고 목록 4. 검색 조회
    @GetMapping("/stock/storeId={storeId}/saleState={saleState}") // 조회용 // ElasticSearch 활용하는 건 어떨까 // 0은 판매중지, 1인 판매 //
    public ResponseEntity stock(@PathVariable(name = "storeId") int storeId, @PathVariable(name = "saleState") int saleState) {
        try {
            if (storeId == 0) {
                if (saleState == 0 ){
                    // 판매 중지 상품 조회
                    List<StockReport> stockReportList = stockReportRepository.findByProductSaleState((byte) 0);
                    List<StockReportResponseDTO> stockReportResponseDTOList = hqControllerStockService.getStockReportResponseDTOList(stockReportList);
                    return new ResponseEntity<>(stockReportResponseDTOList, HttpStatus.OK);
                } else if (saleState == 1){
                    // 판매 상품 조회
                    List<StockReport> stockReportList = stockReportRepository.findByProductSaleState((byte) 1);
                    List<StockReportResponseDTO> stockReportResponseDTOList = hqControllerStockService.getStockReportResponseDTOList(stockReportList);
                    return new ResponseEntity<>(stockReportResponseDTOList, HttpStatus.OK);
                } else if (saleState == 2){
                    //  모든 상품 조회
                    List<StockReport> stockReportList = stockReportRepository.findAll();
                    List<StockReportResponseDTO> stockReportResponseDTOList = hqControllerStockService.getStockReportResponseDTOList(stockReportList);
                    return new ResponseEntity<>(stockReportResponseDTOList, HttpStatus.OK);
                }

            } else if(storeId != 0) {
                // store_id 값을 가진 경우
                if (saleState == 0) {
                    List<StockReport> stockReportByStoreIdList = stockReportRepository.findByStoreIdAndProductSaleState((long) storeId,(byte) 0);
                    List<StockReportResponseDTO> stockReportResponseDTOList = hqControllerStockService.getStockReportResponseDTOList(stockReportByStoreIdList);
                    return new ResponseEntity<>(stockReportResponseDTOList, HttpStatus.OK);
                } else if (saleState == 1) {
                    List<StockReport> stockReportByStoreIdList = stockReportRepository.findByStoreIdAndProductSaleState((long) storeId,(byte) 1);
                    List<StockReportResponseDTO> stockReportResponseDTOList = hqControllerStockService.getStockReportResponseDTOList(stockReportByStoreIdList);
                    return new ResponseEntity<>(stockReportResponseDTOList, HttpStatus.OK);
                } else if (saleState == 2) {
                    List<StockReport> stockReportByStoreIdList = stockReportRepository.findByStoreId((long) storeId);
                    List<StockReportResponseDTO> stockReportResponseDTOList = hqControllerStockService.getStockReportResponseDTOList(stockReportByStoreIdList);
                    return new ResponseEntity<>(stockReportResponseDTOList, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 매출관리
    // 전체, 가게별 / 기간 : 전체, 1주일, 1달, 3달, 기간별
    // 1. 매출 추이(매출 기간별) 2. 점포별(매출 기간별로 @PathVariable) 3. 매출목록(storeId, term)
    // 1. 매출 추이(매출 기간별)
    @GetMapping("/sale-management/storeId={storeId}/date={date}")
    public ResponseEntity saleManagement(@PathVariable(name = "storeId") int storeId, @PathVariable(name = "date") String date) {
        // 기간별 조회시 String 타입으로 "yyyymmddyyyymmdd" 입력값 받는다. ex. "2023010120230401"
        try {
            if (storeId == 0) {
                // storeId == 0 전체 조회
                if(date == "1week") {
                    // 1주일

                }
                if (date == "1month") {
                    // 1달
                }
                if (date == "3month") {
                    // 3달
                } else {
                    // 기간별 조회
                }
                // 기간별 조회
            } else {
                // storeId 값을 지닌 경우
                // storeId == 0 전체 조회
                if(date == "1week") {
                    // 1주일
                }
                if (date == "1month") {
                    // 1달
                }
                if (date == "3month") {
                    // 3달
                } else {
                    // 기간별 조회
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
