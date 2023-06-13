package com.ssg.webpos.controller.admin;
import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.OrderStatus;
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
import com.ssg.webpos.service.managerController.CancelOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/v1/manager")
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
    private final CancelOrderService cancelOrderService;

    // 주문취소
    // 주문 취소 이력을 추적하고 분석할 수 있도록 취소 열을 추가
    // 구매 내역 시간, 구매 취소 시간이 나오도록 별도의 열을 추가하는 게 좋겠다.
    /**
     * 매출 내역을 계산할 때는 취소된 것까지 모두 포함해서 계산
     * 일단 merchant_uid는 serialNumber와 같게 설정했다. 추후에 변경해야한다.
     * 주문 취소가 진행되면 주문 취소 영수증이 나오도록 하는 것
     * 1. merchat_uid 조회
     * 2. 취소 진행
     * 3. 취소 영수증 발급(프런트엔드에서는 '주문이 취소되었습니다.' 알림창과 함께 주문 취소 영수증 발급)
     * **/
    @GetMapping("/order-cancel")
    public ResponseEntity cancelOrder(@RequestParam("merchantUid") String merchantUid) {
        try {
            // 202306011011410202
            Order order = orderRepository.findByMerchantUid(merchantUid);
            System.out.println(order);
            // 가격과 관련된 건 모두 - 붙임
            // orderStaus는 CANCEL
            // orderId는 auto increment이므로 생략함
            int charge = order.getCharge();
            int couponUsePrice = order.getCouponUsePrice();
            int finalTotalPrice = order.getFinalTotalPrice();
            String payMethod = order.getPayMethod().toString();
            int profit = order.getProfit();
            int totalOriginPrice = order.getTotalOriginPrice();
            int totalPrice = order.getTotalPrice();
            int pointUsePrice = order.getPointUsePrice();
            String orderDate = order.getOrderDate().toString();
            LocalDateTime createdDate = LocalDateTime.now();
            LocalDateTime lastModifiedDate = LocalDateTime.now();
            Long posId = order.getPos().getId().getPos_id();
            Long storeId = order.getPos().getId().getStore_id();
            // nullPointerException 발생
            // userId는 if 문 활용
            // deliveryId는 if 문 활용하기
            // 원래 userId의 타입은 long이었는데 null 값때문에 Long으로 변경했다.
            if (order.getDelivery() != null) {
                if (order.getUser() != null) {
                    // user_id가 존재한다면
                    Long deliveryId = order.getDelivery().getId();
                    Long userId = order.getUser().getId();
                    orderRepository.insertOrderCancel(-charge,-couponUsePrice,-finalTotalPrice,"CANCEL",payMethod,-profit,-totalOriginPrice,-totalPrice,-pointUsePrice,orderDate,
                            createdDate,lastModifiedDate,posId,storeId,userId,deliveryId,merchantUid);
                } else if (order.getUser() == null) {
                    // user_id가 존재하지않는다면
                    Long deliveryId = order.getDelivery().getId();;
                    orderRepository.insertOrderCancel(-charge,-couponUsePrice,-finalTotalPrice,"CANCEL",payMethod,-profit,-totalOriginPrice,-totalPrice,-pointUsePrice,orderDate,
                            createdDate,lastModifiedDate,posId,storeId,null,deliveryId,merchantUid);
                }
            } else if(order.getDelivery() == null) {
                if (order.getUser() != null) {
                    // user_id가 존재한다면
                    Long userId = order.getUser().getId();
                    orderRepository.insertOrderCancel(-charge,-couponUsePrice,-finalTotalPrice,"CANCEL",payMethod,-profit,-totalOriginPrice,-totalPrice,-pointUsePrice,orderDate,
                            createdDate,lastModifiedDate,posId,storeId,userId,null,merchantUid);
                } else if (order.getUser() == null) {
                    // user_id가 존재하지않는다면
                }
                orderRepository.insertOrderCancel(-charge,-couponUsePrice,-finalTotalPrice,"CANCEL",payMethod,-profit,-totalOriginPrice,-totalPrice,-pointUsePrice,orderDate,
                        createdDate,lastModifiedDate,posId,storeId,null,null,merchantUid);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 영수증 재발급(order_state가 SUCCESS인 것만 조회)
    // 만약 주문을 취소했다면 취소 영수증이 나오게 하는건 어떨까..-> 찾아보니 어려울 것 같다.
    @GetMapping("/receipt")
    public ResponseEntity receipt(@RequestParam String merchantUid) {
        try {
            //202306013160300101
            OrderDetailResponseDTO orderDetailResponseDTO = new OrderDetailResponseDTO();
            Order order = orderRepository.findByMerchantUidAndOrderStatus(merchantUid, OrderStatus.SUCCESS);

            Long orderId = order.getId();
            Long storeId = order.getPos().getStore().getId();
            Optional<Store> store = storeRepository.findById(storeId);
            orderDetailResponseDTO.setMerchantUid(merchantUid);
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
            if (order.getUser() == null) {
                // 사용자가 존재하지 않는 경우
                orderDetailResponseDTO.setUserName(" ");
                orderDetailResponseDTO.setUserPoint(0);
            } else if (order.getUser() != null){
                User user = order.getUser();
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
            orderDetailResponseDTO.setStoreTelNumber(store.get().getTelNumber());
            orderDetailResponseDTO.setBusinessNumber(store.get().getBusinessNumber());
            orderDetailResponseDTO.setOrderSerialNumber(order.getSerialNumber());
            orderDetailResponseDTO.setCeoName(store.get().getCeoName());
            return new ResponseEntity<>(orderDetailResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
    // 주문 취소한 주문 영수증 발급
    // 기존 영수증과 다른 점은 영수증(취소) 문구와 주문 취소 일자
    @GetMapping("/receipt-cancel")
    public ResponseEntity receiptCancel(@RequestParam String merchantUid) {
        try {
            // 202306011011410202
            OrderDetailResponseDTO orderDetailResponseDTO = new OrderDetailResponseDTO();
            Order order = orderRepository.findFirstByMerchantUidAndOrderStatusOrderByOrderDateDesc(merchantUid, OrderStatus.CANCEL);

            Long orderId = order.getId();
            Long storeId = order.getPos().getStore().getId();
            Optional<Store> store = storeRepository.findById(storeId);
            orderDetailResponseDTO.setMerchantUid(merchantUid);
            orderDetailResponseDTO.setOrderDate(order.getOrderDate());
            orderDetailResponseDTO.setTotalPrice(order.getTotalPrice());
            orderDetailResponseDTO.setCouponUsePrice(order.getCouponUsePrice());
            orderDetailResponseDTO.setPointUsePrice(order.getPointUsePrice());
            orderDetailResponseDTO.setFinalTotalPrice(order.getFinalTotalPrice());
            orderDetailResponseDTO.setCancelDate(order.getOrderDate());
            List<OrderDetailProductResponseDTO> orderDetailProductResponseDTOList = new ArrayList<>();
            //merchantUid로 주문 내역 조회
            Order orderSuccess = orderRepository.findFirstByMerchantUidAndOrderStatusOrderByOrderDateDesc(merchantUid, OrderStatus.SUCCESS);
            Long orderSuccessId = orderSuccess.getId();

            List<Cart> cartList = cartRepository.findAllByOrderId(orderSuccessId);
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
            if (order.getUser() == null) {
                // 사용자가 존재하지 않는 경우
                orderDetailResponseDTO.setUserName(" ");
                orderDetailResponseDTO.setUserPoint(0);
            } else if (order.getUser() != null){
                User user = order.getUser();
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
            orderDetailResponseDTO.setStoreTelNumber(store.get().getTelNumber());
            orderDetailResponseDTO.setCeoName(store.get().getCeoName());
            orderDetailResponseDTO.setOrderSerialNumber(order.getSerialNumber());
            orderDetailResponseDTO.setBusinessNumber(store.get().getBusinessNumber());
            orderDetailResponseDTO.setCancelDate(order.getOrderDate());
            orderDetailResponseDTO.setOrderSerialNumber(orderSuccess.getSerialNumber());
            return new ResponseEntity<>(orderDetailResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }


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
    public ResponseEntity getOrderDetail(@RequestParam String merchantUid) {
        try {
        OrderDetailResponseDTO orderDetailResponseDTO = new OrderDetailResponseDTO();
        Order order = orderRepository.findByMerchantUid(merchantUid);
        User user = order.getUser();
        Long orderId = order.getId();
        Long storeId = order.getPos().getStore().getId();
        Optional<Store> store = storeRepository.findById(storeId);
//        orderDetailResponseDTO.setSerialNumber(serialNumber);
        orderDetailResponseDTO.setOrderDate(order.getOrderDate());
        orderDetailResponseDTO.setTotalPrice(order.getTotalPrice());
        orderDetailResponseDTO.setCouponUsePrice(order.getCouponUsePrice());
        orderDetailResponseDTO.setPointUsePrice(order.getPointUsePrice());
        orderDetailResponseDTO.setFinalTotalPrice(order.getFinalTotalPrice());
        //
        orderDetailResponseDTO.setCardName(order.getCardName());
        orderDetailResponseDTO.setCardNumber(order.getCardNumber());
        orderDetailResponseDTO.setOrderSerialNumber(order.getSerialNumber());
        // 적립 예정은 계산하면댐
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
            //
            orderDetailProduct.setCartQty(cart.getQty());
            orderDetailProduct.setOriginPrice(product.get().getOriginPrice());
            //
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
