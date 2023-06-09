package com.ssg.webpos.controller.admin;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.dto.order.OrderDetailProductResponseDTO;
import com.ssg.webpos.dto.order.OrderDetailResponseDTO;
import com.ssg.webpos.dto.order.RequestOrderDTO;
import com.ssg.webpos.dto.settlement.*;
import com.ssg.webpos.dto.stock.modify.ModifyRequestDTO;
import com.ssg.webpos.dto.stock.stockSubmit.SubmitRequestDTO;
import com.ssg.webpos.dto.stock.stockSubmit.SubmitRequestDTOList;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import com.ssg.webpos.repository.ProductRequestRepository;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import com.ssg.webpos.service.OrderService;
import com.ssg.webpos.service.SettlementDayService;
import com.ssg.webpos.service.SettlementMonthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/v1/manager") // 팀장님한테 점장 로그인 구현 기능 받으면 manager에서 branchadmin-manager로 변경
@Slf4j
@RequiredArgsConstructor
public class BranchAdminManagerController {
    // manager 기능 : 재고 조회, 수정, 삭제, 재고 리포트(주말 재고 현황) 제출
    //               정산 조회, 정산 내역 리포트(일별, 월별 정산내역) 제출
    // 리포트가 이미 제출된 경우 버튼을 비활성화
    private final SettlementDayService settlementDayService;
    private final SettlementMonthService settlementMonthService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ProductRequestRepository productRequestRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StockReportRepository stockReportRepository;
    private final CartRepository cartRepository;
    private final PointUseHistoryRepository pointUseHistoryRepository;


    //조건을 명시한 상태에서 settlement_day 내역 GET (조건 : store_id=1L, settlement_date="2023-05-08")
    @GetMapping("/settlementDay-constant-date")
    public ResponseEntity settlementDayByConstantDate() { //점장 로그인 기능 구현시 팀장님 코드 활용
//        팀장님 작성 BranchAdmin branchAdmin = principalDetail.getUser(); 이거 활용해서 로그인한 사람의 소속 가게 store_id불러올 수 있다.
//        팀장님 작성 Long storeId = branchAdmin.getStore().getId();
//        지금 해야하는 것 : 요청 받은 날짜의 결제 내역을 나오게 하는 것
        try {
            List<SettlementDay> settlementDays = settlementDayService.selectByStoreIdAndDay(1L, "2023-05-08");
            List<SettlementDayReportDTO> reportDTOs = new ArrayList<>();

            for (SettlementDay settlementDay : settlementDays) {
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

    // 일별 내역 조회
    // 날짜를 req 받는 상태에서 settlement_day 내역 GET
    // 예시 URL : http://localhost:8080/api/v1/manager/test2?settlementDate=2023-05-08
    // next level : 점장 로그인시 점장의 해당 store_id 정산내역 표시
    @GetMapping("/settlementDay-variable-date")
    public ResponseEntity settlementDayByVariableDate(@RequestParam("settlementDate")String SettlementDate) throws DateTimeParseException {
        // 20022-05-08같이 테이블에 없는 날짜 내역이 올 경우 에러 -> DateTimeParseException
        //
        try {
            List<SettlementDay> settlementDays = settlementDayService.selectByStoreIdAndDay(1L,SettlementDate);
            List<SettlementDayReportDTO> reportDTOs = new ArrayList<>();

            for (SettlementDay settlementDay : settlementDays) {
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

    // 월별 기간 상세 조회
    // store_id, "yyyy-mm"
    @PostMapping("/settlement-month/detail")
    public ResponseEntity getDetailMonth(@RequestBody SettlementMonthDetailRequestDTO requestDTO) throws NullPointerException{
        try {
            Long store_id = requestDTO.getStoreId();
            String date = requestDTO.getDate();

            List<Order> orderList = orderService.selectByOrdersMonth(store_id, date);
            Iterator<Order> iterator = orderList.iterator();
            while (iterator.hasNext()) {
                Order order = iterator.next();
                System.out.println("order = " + order);
            }

            List<SettlementMonthDetailResponseDTO> responseDTOList = orderList.stream()
                    .map(order -> new SettlementMonthDetailResponseDTO(order))
                    .collect(Collectors.toList());
            return new ResponseEntity(responseDTOList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // 일별 기간 상세 조회
    // store_id, "yyyy-mm-dd"
    @PostMapping("/settlement-day/detail")
    public ResponseEntity getOrder(@RequestBody SettlementMonthDetailRequestDTO requestDTO) throws NullPointerException{
        Long store_id = requestDTO.getStoreId();
        String date = requestDTO.getDate();

        List<Order> orderList = orderService.selectByOrdersDay(store_id,date);
        Iterator<Order> iterator = orderList.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            System.out.println("order = " + order);
        }

        List<SettlementMonthDetailResponseDTO> responseDTOList = orderList.stream()
                .map(order -> new SettlementMonthDetailResponseDTO(order))
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseDTOList, HttpStatus.OK);
    }

    // store_id,"yyyy-mm-dd"로 주문내역 조회
    // 이거 orders 전체 칼럼 내용이 나와서 수정해야함
    @PostMapping("/orders")
    public ResponseEntity getOrder(@RequestBody RequestOrderDTO requestOrderDTO) throws NullPointerException{
        try {
            Long storeId = requestOrderDTO.getStoreId();
            String date = requestOrderDTO.getDate();
            List<Order> Order = orderService.selectByPos_StoreIdAndOrderDate(storeId, date);
            return new ResponseEntity(Order,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // orders 목록에서 serial_number를 누르면 상세주문내역이 나온다.그러기 위해서는 serial_nubmer를 req로 받아야한다. storename, 전화번호 검색
    @GetMapping("/orders-detail")
    public ResponseEntity getOrderDetail(@RequestParam String serialNumber) {
        try {
        OrderDetailResponseDTO orderDetailResponseDTO = new OrderDetailResponseDTO();
        Order order = orderRepository.findBySerialNumber(serialNumber);
        User user = order.getUser();
        Long orderId = order.getId();
        Long storeId = order.getPos().getStore().getId();
        Optional<Store> store = storeRepository.findById(storeId);
        orderDetailResponseDTO.setSerialNumber(serialNumber);
        orderDetailResponseDTO.setOrderDate(order.getOrderDate());
        orderDetailResponseDTO.setTotalPrice(order.getTotalPrice());
        orderDetailResponseDTO.setCouponUsePrice(order.getCouponUsePrice());
        orderDetailResponseDTO.setPointUsePrice(order.getPointUsePrice());
        orderDetailResponseDTO.setFinalTotalPrice(order.getFinalTotalPrice());
        List<OrderDetailProductResponseDTO> orderDetailProductResponseDTOList = new ArrayList<>();
        List<Cart> cartList = cartRepository.findAllByOrderId(orderId);
        // 주문 상품의 이름, 수량, 상품 가격 정보 시작
        for (Cart cart : cartList) {
            OrderDetailProductResponseDTO orderDetailProduct = new OrderDetailProductResponseDTO();
            Long productId = cart.getProduct().getId();
            Optional<Product> product = productRepository.findById(productId);
            orderDetailProduct.setProductName(product.get().getName());
            orderDetailProduct.setProductQty(product.get().getStock());
            orderDetailProduct.setProductSalePrice(product.get().getSalePrice());
            orderDetailProductResponseDTOList.add(orderDetailProduct);
        }
        orderDetailResponseDTO.setOrderDetailProductResponseDTOList(orderDetailProductResponseDTOList);
        if (user == null) {
            // 사용자가 존재하지 않는 경우
            orderDetailResponseDTO.setUserName(" ");
            orderDetailResponseDTO.setUserPoint(0);
        } else {
            // 사용자가 존재하는 경우
            orderDetailResponseDTO.setUserName(user.getName());
            orderDetailResponseDTO.setUserPoint(user.getPoint().getPointAmount());
        }
        int finalTotalPrice = order.getFinalTotalPrice();
        int productPrice = finalTotalPrice*10/11;
        int vat = finalTotalPrice*1/11;
        orderDetailResponseDTO.setProductPrice(productPrice);
        orderDetailResponseDTO.setVat(vat);
        orderDetailResponseDTO.setStoreName(store.get().getName());
        orderDetailResponseDTO.setStoreAdress(store.get().getAddress());
        return new ResponseEntity<>(orderDetailResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // store_id별 재고내역 조회
    @GetMapping("/stock-report-view/store-id")
    public ResponseEntity stockReportStoreId(@RequestParam Long storeId) {
        try {
            List<StockReport> stockReport = stockReportRepository.findByStoreId(storeId);
            return new ResponseEntity(stockReport,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // store_id별 재고내역 수정(재고수량만 수정 가능)
    // 재고내역 수정하려면 어떤 상품의 재고수량을 수정할 것인지
    // 단일 수정
    @PostMapping("/stock-report-modify/store-id")
    @Transactional
    public ResponseEntity stockReportStoreIdModify(@RequestBody ModifyRequestDTO modifyRequestDTO) {
        try {
            // req받은 stockReportId를 가진 stockReport의 현재 재고 수량을 변경한다. 바로 DB에 넣을꺼양^^
            Optional<StockReport> stockReport = stockReportRepository.findById(modifyRequestDTO.getStockReportId());
            stockReport.get().setCurrentStock(modifyRequestDTO.getCurrentStock());
            stockReportRepository.saveAndFlush(stockReport.get());
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 발주 신청
    @PostMapping("/stock-report/submit")
    public ResponseEntity stockReportSubmit(@RequestBody SubmitRequestDTOList submitRequestDTOList) {
        try {
            List<SubmitRequestDTO> submitRequestDTOs = submitRequestDTOList.getSubmitRequestDTOList();
            List<ProductRequest> productRequests = new ArrayList<>();

            for(SubmitRequestDTO submitRequestDTO : submitRequestDTOs) {
                ProductRequest productRequest = new ProductRequest();
                productRequest.setQty(submitRequestDTO.getQty());
                Optional<Product> findProduct = productRepository.findById(submitRequestDTO.getProductId());
                productRequest.setProduct(findProduct.get()); // 양방향 연관관계 필요없음(product에서 product_request 조회x)
                Optional<Store> store = storeRepository.findById(submitRequestDTOList.getStoreId());
                productRequest.addStoreWithAssociation(store.get());
                productRequests.add(productRequest);
            }
            productRequestRepository.saveAll(productRequests);

            for (SubmitRequestDTO submitRequestDTO : submitRequestDTOs) {
                Optional<StockReport> findStockReport = stockReportRepository.findById(submitRequestDTO.getStockReportId());
                StockReport stockReport = findStockReport.get();
                stockReport.setSubmit(true);
            }
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
