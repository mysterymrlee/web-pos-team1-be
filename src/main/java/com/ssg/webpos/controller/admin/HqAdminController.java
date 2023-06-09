package com.ssg.webpos.controller.admin;

import com.ssg.webpos.config.jwt.JwtUtil;
import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.Role;
import com.ssg.webpos.dto.*;
import com.ssg.webpos.dto.hqMain.AllAndStoreDTO;
import com.ssg.webpos.dto.hqMain.SettlementByTermDTO;
import com.ssg.webpos.dto.hqMain.SettlementByTermListDTO;
import com.ssg.webpos.dto.hqMain.StoreDTO;
import com.ssg.webpos.dto.hqSale.HqSaleByStoreNameDTO;
import com.ssg.webpos.dto.hqSale.HqSaleOrderDTO;
import com.ssg.webpos.dto.hqSale.HqSettlementDayDTO;
import com.ssg.webpos.dto.hqStock.StockReportResponseDTO;
import com.ssg.webpos.dto.hqStock.StockReportUpdateRequestDTO;
import com.ssg.webpos.dto.settlement.*;
import com.ssg.webpos.dto.stock.AllStockReportResponseDTO;
import com.ssg.webpos.dto.stock.StoreIdStockReportResponseDTO;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.repository.cart.CartRedisImplRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import com.ssg.webpos.service.HQAdminService;
import com.ssg.webpos.service.SettlementDayService;
import com.ssg.webpos.service.SettlementMonthService;
import com.ssg.webpos.service.hqController.method.HqControllerStockService;
import com.ssg.webpos.service.hqController.method.SaleMethodService;
import com.ssg.webpos.service.hqController.method.StockReportUpdateService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.nio.file.attribute.UserPrincipalNotFoundException;
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
    private final ProductRepository productRepository;
    private final StockReportUpdateService stockReportUpdateService;
    private final SaleMethodService saleMethodService;
    private final HQAdminService hqAdminService;
    private final CartRedisImplRepository cartRedisImplRepository;
    private final JwtUtil jwtUtil;
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
    //  default : 전체  // 1. 전체 매출,store_id별 매출 2. 판매 여부 조회 3. 재고 목록
    @GetMapping("/stock") // 조회용 // ElasticSearch 활용하는 건 어떨까 // 0은 판매중지, 1인 판매 //
    public ResponseEntity stock(@RequestParam(name = "storeId") int storeId, @RequestParam(name = "saleState") int saleState, @RequestParam(name = "order") String order) {
        try {
            if (storeId == 0) {
                // 모든 store 조회
                if (saleState == 0 ){
                    // 판매 중지 상품 조회
                    if(order.equals("salePriceASC")) {
                        // 판매가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceAscBySaleState(0);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("salePriceDESC")) {
                        // 판매가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceDescBySaleState(0);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceASC")) {
                        // 원가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceAscBySaleState(0);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceDESC")) {
                        // 원가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceDescBySaleState(0);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockASC")) {
                        // 재고수량 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockAscBySaleState(0);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockDESC")) {
                        // 재고수량 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockDescBySaleState(0);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("none")){
                        // 정렬이 없는 경우
                        List<Product> productList = productRepository.findProductBySalePrice(0);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    }
                } else if (saleState == 1){
                    // 판매 상품 조회
                    if(order.equals("salePriceASC")) {
                        // 판매가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceAscBySaleState(1);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("salePriceDESC")) {
                        // 판매가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceDescBySaleState(1);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceASC")) {
                        // 원가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceAscBySaleState(1);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceDESC")) {
                        // 원가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceDescBySaleState(1);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockASC")) {
                        // 재고수량 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockAscBySaleState(1);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockDESC")) {
                        // 재고수량 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockDescBySaleState(1);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("none")){
                        // 정렬이 없는 경우
                        List<Product> productList = productRepository.findProductBySalePrice(1);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    }
                } else if (saleState == 2){
                    //  모든 상품 조회
                    if(order.equals("salePriceASC")) {
                        // 판매가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceAsc();
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("salePriceDESC")) {
                        // 판매가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceDesc();
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceASC")) {
                        // 원가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceAsc();
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceDESC")) {
                        // 원가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceDesc();
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockASC")) {
                        // 재고수량 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockAsc();
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockDESC")) {
                        // 재고수량 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockDesc();
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("none")){
                        // 정렬이 없는 경우
                        List<Product> productList = productRepository.findProduct();
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    }
                }

            } else if(storeId != 0) {
                // store_id 값을 가진 경우
                if (saleState == 0) {
                    // 판매 중지 상품 조회
                    if(order.equals("salePriceASC")) {
                        // 판매가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceAscBySaleStateAndStoreId(0, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("salePriceDESC")) {
                        // 판매가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceDescBySaleStateAndStoreId(0, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceASC")) {
                        // 원가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceAscBySaleStateAndStoreId(0, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceDESC")) {
                        // 원가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceDescBySaleStateAndStoreId(0, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockASC")) {
                        // 재고수량 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockAscBySaleStateAndStoreId(0, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockDESC")) {
                        // 재고수량 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockDescBySaleStateAndStoreId(0, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("none")){
                        // 정렬이 없는 경우
                        List<Product> productList = productRepository.findProductBySaleStateAndStoreId(0,storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    }

                } else if (saleState == 1) {
                    // 판매 중 상품 조회
                    if(order.equals("salePriceASC")) {
                        // 판매가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceAscBySaleStateAndStoreId(1, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("salePriceDESC")) {
                        // 판매가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceDescBySaleStateAndStoreId(1, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceASC")) {
                        // 원가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceAscBySaleStateAndStoreId(1, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceDESC")) {
                        // 원가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceDescBySaleStateAndStoreId(1, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockASC")) {
                        // 재고수량 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockAscBySaleStateAndStoreId(1, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockDESC")) {
                        // 재고수량 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockDescBySaleStateAndStoreId(1, storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("none")){
                        // 정렬이 없는 경우
                        List<Product> productList = productRepository.findProductBySaleStateAndStoreId(1,storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    }
                } else if (saleState == 2) {
                    // 모든 상품 조회
                    if(order.equals("salePriceASC")) {
                        // 판매가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceAscByStoreId(storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("salePriceDESC")) {
                        // 판매가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderBySalePriceDescByStoreId(storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceASC")) {
                        // 원가 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceAscByStoreId(storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("originPriceDESC")) {
                        // 원가 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByOriginPriceDescByStoreId(storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockASC")) {
                        // 재고수량 오름차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockAscByStoreId(storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("stockDESC")) {
                        // 재고수량 내림차순 정렬
                        List<Product> productList = productRepository.findProductsOrderByStockDescByStoreId(storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    } else if (order.equals("none")){
                        // 정렬이 없는 경우
                        List<Product> productList = productRepository.findProductByStoreId(storeId);
                        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
                        return new ResponseEntity(stockReportResponseDTO, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    // 재고관리에서 수정
    // 상품명, 판매가, 원가 수정 후 DB에 반영, product 반영(DTO 를 생성할 떄 stockReport가 아닌 product에서 가져오기에)
    // 프런트앤드에서 모든 필드값을 지닌 DTO를 던져줘야한다.
    @PostMapping("/stock/modify")
    public ResponseEntity stockModify(@RequestBody StockReportUpdateRequestDTO stockReportUpdateRequestDTO) {
        // DTO를 받으면 그 DTO 내용을 DB에 적용시키는 서비스를 만들기
        try {
            stockReportUpdateService.updateStockReport(stockReportUpdateRequestDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 매출관리
    // 전체, 가게별 / 기간 : 전체, 1주일, 1달, 3달, 기간별
    // 1. 매출 추이(매출 기간별) 2. 점포별(매출 기간별로 @PathVariable) 3. 매출목록(storeId, term)
    // 1. 매출 추이(매출 기간별)
    @GetMapping("/sale-management/storeId={storeId}/date={date}/startDate={startDate}/endDate={endDate}")
    public ResponseEntity saleManagement(@PathVariable(name = "storeId") int storeId, @PathVariable(name = "date") String date, @PathVariable(name = "startDate") String startDate, @PathVariable(name = "endDate") String endDate) {
        // 기간별 조회시 String 타입으로 "yyyymmddyyyymmdd" 입력값 받는다. ex. "2023010120230401"
        try {
            if (storeId == 0) {
                // 전체 조회
                if(date.equals("1week")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 어제의 일주일 전부터 어제까지의 일일 매출 내역
                    List<Object[]> list = settlementDayRepository.settlementDay1Week();
                    List<HqSettlementDayDTO> hqSettlementDayDTOList = saleMethodService.saleMethod(list);
                    return new ResponseEntity<>(hqSettlementDayDTOList, HttpStatus.OK);
                }
                if (date.equals("1month")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 1달
                    List<Object[]> list = settlementDayRepository.settlementDay1Month();
                    List<HqSettlementDayDTO> hqSettlementDayDTOList = saleMethodService.saleMethod(list);
                    return new ResponseEntity<>(hqSettlementDayDTOList, HttpStatus.OK);
                }
                if (date.equals("3month")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 3달
                    List<Object[]> list = settlementDayRepository.settlementDay3Month();
                    List<HqSettlementDayDTO> hqSettlementDayDTOList = saleMethodService.saleMethod(list);
                    return new ResponseEntity<>(hqSettlementDayDTOList, HttpStatus.OK);

                }
                if (date.equals("term")&&startDate.equals(startDate)&&endDate.equals(endDate)) {
                    // 기간별 조회
                    // "2023-05-01" 형식 희망
                    List<Object[]> list = settlementDayRepository.settlementDayTerm(startDate, endDate);
                    List<HqSettlementDayDTO> hqSettlementDayDTOList = saleMethodService.saleMethod(list);
                    return new ResponseEntity<>(hqSettlementDayDTOList, HttpStatus.OK);
                } else {
                    // 그 외에 들어오는 값은 안내 메세지 넣기
                    // ex. 적절하지 않은 조회입니다
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            } else if (storeId != 0 ){
                // storeId 값을 지닌 경우
                // storeId == 0 전체 조회
                if(date.equals("1week")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 1주일, store_id로 조회
                    List<Object[]> list = settlementDayRepository.settlementDay1WeekByStoreId(storeId);
                    List<HqSettlementDayDTO> hqSettlementDayDTOList = saleMethodService.saleMethod(list);
                    return new ResponseEntity<>(hqSettlementDayDTOList, HttpStatus.OK);
                }
                if (date.equals("1month")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 1달, store_id로 조회
                    List<Object[]> list = settlementDayRepository.settlementDay1MonthByStoreId(storeId);
                    List<HqSettlementDayDTO> hqSettlementDayDTOList = saleMethodService.saleMethod(list);
                    return new ResponseEntity<>(hqSettlementDayDTOList, HttpStatus.OK);
                }
                if (date.equals("3month")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 3달, store_id로 조회
                    List<Object[]> list = settlementDayRepository.settlementDay3MonthByStoreId(storeId);
                    List<HqSettlementDayDTO> hqSettlementDayDTOList = saleMethodService.saleMethod(list);
                    return new ResponseEntity<>(hqSettlementDayDTOList, HttpStatus.OK);
                }
                if (date.equals("term")&&startDate.equals(startDate)&&endDate.equals(endDate)) {
                    // 기간별 조회
                    List<Object[]> list = settlementDayRepository.settlementDayTermByStoreId(startDate,endDate,storeId);
                    List<HqSettlementDayDTO> hqSettlementDayDTOList = new ArrayList<>();
                    for (Object[] objects : list) {
                        HqSettlementDayDTO hqSettlementDayDTO = new HqSettlementDayDTO();
                        java.sql.Date sqlDate = (java.sql.Date) objects[0];
                        LocalDate settlementDayDate = sqlDate.toLocalDate();
                        hqSettlementDayDTO.setSettlementDayDate(settlementDayDate);
                        Integer settlementPriceInteger = (Integer) objects[1];
                        BigDecimal settlementPrice = BigDecimal.valueOf(settlementPriceInteger);
                        hqSettlementDayDTO.setSettlementDaySettlementPrice(settlementPrice.intValue());
                        hqSettlementDayDTOList.add(hqSettlementDayDTO);
                    }
                    return new ResponseEntity<>(hqSettlementDayDTOList, HttpStatus.OK);
                }else {
                    // 그 외에 들어오는 값은 안내 메세지 넣기
                    // ex. 적절하지 않은 조회입니다.
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/sale-management/pie-chart/date={date}/startDate={startDate}/endDate={endDate}")
    public ResponseEntity pieChart(@PathVariable("date") String date, @PathVariable(name = "startDate") String startDate, @PathVariable(name = "endDate") String endDate) {
        try {
            if (date.equals("1week")&&startDate.equals("0")&&endDate.equals("0")) {
                // 어제의 일주일 전부터 어제까지의 지점별 매출합
                List<Object[]> settlementDayList = settlementDayRepository.Sale1WeekForPieChart();
                List<HqSaleByStoreNameDTO> list = saleMethodService.pieChartMethod(settlementDayList);
                return new ResponseEntity<>(list,HttpStatus.OK);
            } if (date.equals("1month")&&startDate.equals("0")&&endDate.equals("0")) {
                // 어제의 한달 전부터 어제까지의 지점별 매출합
                List<Object[]> settlementDayList = settlementDayRepository.Sale1MonthForPieChart();
                List<HqSaleByStoreNameDTO> list = saleMethodService.pieChartMethod(settlementDayList);
                return new ResponseEntity<>(list,HttpStatus.OK);
            } if (date.equals("3month")&&startDate.equals("0")&&endDate.equals("0")) {
                // 어제의 세달 전부터 어제까지의 지점별 매출합
                List<Object[]> settlementDayList = settlementDayRepository.Sale3MonthForPieChart();
                List<HqSaleByStoreNameDTO> list = saleMethodService.pieChartMethod(settlementDayList);
                return new ResponseEntity<>(list,HttpStatus.OK);
            } if (date.equals("term")&&startDate.equals(startDate)&&endDate.equals(endDate)) {
                // 기간별 지점별 매출합
                List<Object[]> settlementDayList = settlementDayRepository.SaleTermForPieChart(startDate,endDate);
                List<HqSaleByStoreNameDTO> list = saleMethodService.pieChartMethod(settlementDayList);
                return new ResponseEntity<>(list,HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 매출 목록 조회
    @GetMapping("/sale-management/list/date={date}/storeId={storeId}/startDate={startDate}/endDate={endDate}")
    public ResponseEntity saleOrderListView(@PathVariable("date") String date, @PathVariable("storeId") int storeId,@PathVariable("startDate") String startDate,@PathVariable("endDate") String endDate) {
        try {
            if(storeId == 0) {
                if (date.equals("1week")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 어제의 일주일 전부터 어제까지의 전체 매출 기록
                    List<Order> orderList = orderRepository.allStoreOrderBy1Week();
                    List<HqSaleOrderDTO> list = saleMethodService.orderListMethod(orderList);
                    return new ResponseEntity(list, HttpStatus.OK);
                } if (date.equals("1month")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 어제의 한달 전부터 어제까지의 전체 매출 기록
                    List<Order> orderList = orderRepository.allStoreOrderBy1Month();
                    List<HqSaleOrderDTO> list = saleMethodService.orderListMethod(orderList);
                    return new ResponseEntity(list, HttpStatus.OK);

                } if (date.equals("3month")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 어제의 세달 전부터 어제까지의 전체 매출 기록
                    List<Order> orderList = orderRepository.allStoreOrderBy3Month();
                    List<HqSaleOrderDTO> list = saleMethodService.orderListMethod(orderList);
                    return new ResponseEntity(list, HttpStatus.OK);

                } if (date.equals("term")&&startDate.equals(startDate)&&endDate.equals(endDate)) {
                    // 기간별 전체 매출 기록
                    List<Order> orderList = orderRepository.allStoreOrderByTerm(startDate,endDate);
                    List<HqSaleOrderDTO> list = saleMethodService.orderListMethod(orderList);
                    return new ResponseEntity(list, HttpStatus.OK);

                }
            } else if(storeId !=0 ) {
                if (date.equals("1week")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 어제의 일주일 전부터 어제까지의 store_id별 매출 기록
                    List<Order> orderList = orderRepository.allStoreOrderBy1WeekByStoreId(storeId);
                    List<HqSaleOrderDTO> list = saleMethodService.orderListMethod(orderList);
                    return new ResponseEntity(list, HttpStatus.OK);

                } if (date.equals("1month")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 어제의 한달 전부터 어제까지의 store_id별 매출 기록
                    List<Order> orderList = orderRepository.allStoreOrderBy1MonthByStoreId(storeId);
                    List<HqSaleOrderDTO> list = saleMethodService.orderListMethod(orderList);
                    return new ResponseEntity(list, HttpStatus.OK);

                } if (date.equals("3month")&&startDate.equals("0")&&endDate.equals("0")) {
                    // 어제의 세달 전부터 어제까지의 store_id별 매출 기록
                    List<Order> orderList = orderRepository.allStoreOrderBy3MonthByStoreId(storeId);
                    List<HqSaleOrderDTO> list = saleMethodService.orderListMethod(orderList);
                    return new ResponseEntity(list, HttpStatus.OK);

                } if (date.equals("term")&&startDate.equals(startDate)&&endDate.equals(endDate)) {
                    // 기간별 store_id별 매출 기록
                    List<Order> orderList = orderRepository.allStoreOrderByTermByStoreId(startDate,endDate,storeId);
                    List<HqSaleOrderDTO> list = saleMethodService.orderListMethod(orderList);
                    return new ResponseEntity(list, HttpStatus.OK);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 전체 월별정산내역 조회
    // "2023"을 받으면 2023년에 생성된 전체 월별정산내역 조회
//    @PostMapping("/settlement-month/all-store")
//    public ResponseEntity settlementMonth(@RequestBody RequsestSettlementMonthDTO requestSettlementMonthDTO) {
//        try {
//            String year = requestSettlementMonthDTO.getYear();
//            List<SettlementMonth> settlementMonths = settlementMonthService.selectByYear(year);
//            List<SettlementMonthReportDTO> reportDTOs = new ArrayList<>();
//
//            for(SettlementMonth settlementMonth:settlementMonths) {
//                SettlementMonthReportDTO reportDTO = new SettlementMonthReportDTO();
//                reportDTO.setSettlementMontnId(settlementMonth.getId());
//                reportDTO.setSettlementPrice(settlementMonth.getSettlementPrice());
//                reportDTO.setSettlementDate(settlementMonth.getSettlementDate());
//                reportDTO.setStoreId(settlementMonth.getStore().getId());
//                reportDTO.setStoreName(settlementMonth.getStore().getName());
//                reportDTO.setCreatedDate(settlementMonth.getCreatedDate());
//                reportDTOs.add(reportDTO);
//            }
//            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
//        } catch (Exception e) {
//            e.getStackTrace();
//            e.printStackTrace();
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
//    }

    // store_id별 월별정산내역 조회
    // 1L, "2023"을 받으면 store_id=1L인 가게의 2023년 발생 월별정산내역 조회
//    @PostMapping("/settlement-month/store-id")
//    public ResponseEntity settlementMonthByStoreId(@RequestBody RequestSettlementMonthByStoreIdDTO requestSettlementMonthByStoreIdDTO) {
//        try {
//            String year = requestSettlementMonthByStoreIdDTO.getYear();
//            Long storeId = requestSettlementMonthByStoreIdDTO.getStoreId();
//            List<SettlementMonth> settlementMonths = settlementMonthService.selectByStoreIdAndDayBetween(storeId, year);
//            List<SettlementMonthReportDTO> reportDTOs = new ArrayList<>();
//
//            for(SettlementMonth settlementMonth:settlementMonths) {
//                SettlementMonthReportDTO reportDTO = new SettlementMonthReportDTO();
//                reportDTO.setSettlementMontnId(settlementMonth.getId());
//                reportDTO.setSettlementPrice(settlementMonth.getSettlementPrice());
//                reportDTO.setSettlementDate(settlementMonth.getSettlementDate());
//                reportDTO.setStoreId(settlementMonth.getStore().getId());
//                reportDTO.setStoreName(settlementMonth.getStore().getName());
//                reportDTO.setCreatedDate(settlementMonth.getCreatedDate());
//                reportDTOs.add(reportDTO);
//            }
//            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
//    }

    // 전체 기간별 월별정산내역 조회
    // "2023-02","2023-03" 받으면 2023-02부터 2023-03의 전체 월별정산내역 조회
//    @PostMapping("/settlement-month/range/all-store")
//    public ResponseEntity settlementMonthRange(@RequestBody RequestSettlementMonthRangeDTO requestSettlementMonthRangeDTO) {
//        try {
//            String StartDate = requestSettlementMonthRangeDTO.getStartDate();
//            String EndDate = requestSettlementMonthRangeDTO.getEndDate();
//            List<SettlementMonth> settlementMonths = settlementMonthService.selectByMonthRange(StartDate,EndDate);
//            List<SettlementMonthReportDTO> reportDTOs = new ArrayList<>();
//
//            for(SettlementMonth settlementMonth:settlementMonths) {
//                SettlementMonthReportDTO reportDTO = new SettlementMonthReportDTO();
//                reportDTO.setSettlementMontnId(settlementMonth.getId());
//                reportDTO.setSettlementPrice(settlementMonth.getSettlementPrice());
//                reportDTO.setSettlementDate(settlementMonth.getSettlementDate());
//                reportDTO.setStoreId(settlementMonth.getStore().getId());
//                reportDTO.setStoreName(settlementMonth.getStore().getName());
//                reportDTO.setCreatedDate(settlementMonth.getCreatedDate());
//                reportDTOs.add(reportDTO);
//            }
//
//            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
//    }

    // store_id별 기간별 월별정산내역 조회
    // 1L, "2023-02","2023-03" 받으면 store_id=1L이고 2023-02부터 2023-03의 월별정산내역 조회
//    @PostMapping("/settlement-month/range/store_id")
//    public ResponseEntity settlementMonthRangeByStoreId(@RequestBody RequestSettlementMonthRangeByStoreIdDTO requestSettlementMonthRangeByStoreIdDTO) {
//        try {
//            String StartDate = requestSettlementMonthRangeByStoreIdDTO.getStartDate();
//            String EndDate = requestSettlementMonthRangeByStoreIdDTO.getEndDate();
//            Long storeId = requestSettlementMonthRangeByStoreIdDTO.getStoreId();
//            List<SettlementMonth> settlementMonths = settlementMonthService.selectByStoreIdAndMonthRange(storeId,StartDate,EndDate);
//            List<SettlementMonthReportDTO> reportDTOs = new ArrayList<>();
//
//            for(SettlementMonth settlementMonth:settlementMonths) {
//                SettlementMonthReportDTO reportDTO = new SettlementMonthReportDTO();
//                reportDTO.setSettlementMontnId(settlementMonth.getId());
//                reportDTO.setSettlementPrice(settlementMonth.getSettlementPrice());
//                reportDTO.setSettlementDate(settlementMonth.getSettlementDate());
//                reportDTO.setStoreId(settlementMonth.getStore().getId());
//                reportDTO.setStoreName(settlementMonth.getStore().getName());
//                reportDTO.setCreatedDate(settlementMonth.getCreatedDate());
//                reportDTOs.add(reportDTO);
//            }
//
//            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
//    }

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

    /**
     * 전체 재고 조회
     * @return ResponseEntity
     * */
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

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody HQAdminLoginRequestDTO requestDTO) throws UserPrincipalNotFoundException {
        try {
            HQAdminLoginResponseDTO responseDTO = hqAdminService.login(requestDTO);
            return new ResponseEntity(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.getStackTrace();
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
    @GetMapping("/check-is-logined")
    public ResponseEntity checkIsLogined(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("request = " + request);
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.replace("Bearer ", "");
        Claims claims = JwtUtil.extractAllClaims(token);
        // JwtUtil.getEmail(token);
        System.out.println("claims.get(\"number\") = " + claims.get("number"));
        // JwtUtil.getId(token);
        System.out.println("claims.get(\"id\") = " + claims.get("id"));
        HttpStatus status;
        if (!JwtUtil.isExpired(token)) {
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity(claims, status);
    }
    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDto requestDto) {
        if (JwtUtil.isExpired(requestDto.getRefreshToken())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        HttpStatus status = HttpStatus.NOT_IMPLEMENTED; // 501
        try {
            cartRedisImplRepository.deleteToken(requestDto.getRefreshToken());
            status = HttpStatus.NO_CONTENT;
        } catch (IllegalArgumentException e) {
            status = HttpStatus.BAD_REQUEST;
        } finally {
            return new ResponseEntity(status);
        }
    }
    @PostMapping("/reissue")
    public ResponseEntity<ReissueTokenResponseDTO> reissue(@RequestBody RefreshTokenDto requestDto) {
        if (JwtUtil.isExpired(requestDto.getRefreshToken())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED); // 401
        }
        Claims claims = JwtUtil.extractAllClaims(requestDto.getRefreshToken());
        Long id = Long.valueOf(String.valueOf(claims.get("id")));
        String number = String.valueOf(claims.get("number"));
        Role userRole = Role.valueOf(String.valueOf(claims.get("role")));

        String newAccessToken = jwtUtil.generateAccessToken(id, number, userRole);
        ReissueTokenResponseDTO responseDto = new ReissueTokenResponseDTO(newAccessToken, requestDto.getRefreshToken());
        return new ResponseEntity(responseDto, HttpStatus.CREATED);
    }
}
