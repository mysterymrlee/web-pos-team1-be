package com.ssg.webpos.service.hqController.method;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.domain.Store;
import com.ssg.webpos.dto.hqSale.HqListForSaleDTO;
import com.ssg.webpos.dto.hqSale.HqSaleByStoreNameDTO;
import com.ssg.webpos.dto.hqSale.HqSaleOrderDTO;
import com.ssg.webpos.dto.hqSale.HqSettlementDayDTO;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import com.ssg.webpos.service.hqController.csv.CsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaleMethodService {
    private final SettlementDayRepository settlementDayRepository;
    private final StoreRepository storeRepository;
    private final CsvService csvService;

    public List<HqSettlementDayDTO> HqSaleMethods(List<SettlementDay> settlementDayList) {
        List<HqSettlementDayDTO> hqSettlementDayDTOList = new ArrayList<>();
        for (SettlementDay settlementDay : settlementDayList) {
            HqSettlementDayDTO hqSettlementDayDTO = new HqSettlementDayDTO();
            hqSettlementDayDTO.setSettlementDayDate(settlementDay.getSettlementDate());
            hqSettlementDayDTO.setSettlementDaySettlementPrice(settlementDay.getSettlementPrice());
            hqSettlementDayDTOList.add(hqSettlementDayDTO);
        }
        return hqSettlementDayDTOList;
    }

    public List<HqSettlementDayDTO> saleMethod(List<Object[]> objectList) {
        List<HqSettlementDayDTO> hqSettlementDayDTOList = new ArrayList<>();
        for (Object[] objects : objectList) {
            HqSettlementDayDTO hqSettlementDayDTO = new HqSettlementDayDTO();
            java.sql.Date sqlDate = (java.sql.Date) objects[0];
            LocalDate settlementDayDate = sqlDate.toLocalDate();
            hqSettlementDayDTO.setSettlementDayDate(settlementDayDate);
            BigDecimal settlementPrice = (BigDecimal) objects[1];
            hqSettlementDayDTO.setSettlementDaySettlementPrice(settlementPrice.intValue());
            hqSettlementDayDTOList.add(hqSettlementDayDTO);
        }
        return hqSettlementDayDTOList;
    }

    // store_id와 매출합을 가진 Object[]을 DTO에 넣는 매서드입니다.
    public List<HqSaleByStoreNameDTO> pieChartMethod(List<Object[]> objectList) {
        List<HqSaleByStoreNameDTO> hqSaleByStoreNameDTOList = new ArrayList<>();
        for (Object[] object : objectList) {
            HqSaleByStoreNameDTO hqSaleByStoreNameDTO = new HqSaleByStoreNameDTO();
            // DTO에는 int 타입의 settlementPrice, String 타입의 storeName 필드 있다.
            // settlementPrice 필드 과정
            BigDecimal settlementPrice = (BigDecimal) object[0];
            hqSaleByStoreNameDTO.setSettlementPrice(settlementPrice.intValue());
            // storeName 필드 과정
            BigInteger storeId = (BigInteger) object[1];
            BigDecimal decimalStoreId = new BigDecimal(storeId);
            int id = decimalStoreId.intValue();
            Optional<Store> store = storeRepository.findById(Long.valueOf(id));
            String storeName = store.get().getName();
            hqSaleByStoreNameDTO.setStoreName(storeName);
            hqSaleByStoreNameDTOList.add(hqSaleByStoreNameDTO);
        }
        return hqSaleByStoreNameDTOList;
    }

    public List<HqSaleOrderDTO> orderListMethod(List<Order> orderList) {
        List<HqSaleOrderDTO> list = new ArrayList<>();
        for (Order order : orderList) {
            HqSaleOrderDTO hqSaleOrderDTO = new HqSaleOrderDTO();
            hqSaleOrderDTO.setSerialNumber(order.getSerialNumber());
            hqSaleOrderDTO.setStoreName(order.getPos().getStore().getName());
            hqSaleOrderDTO.setOrderDate(order.getOrderDate());
            hqSaleOrderDTO.setOrderStatus(order.getOrderStatus());
            hqSaleOrderDTO.setPayMethod(order.getPayMethod());
            hqSaleOrderDTO.setTotalPrice(order.getTotalPrice());
            hqSaleOrderDTO.setPointUsePrice(order.getPointUsePrice());
            hqSaleOrderDTO.setCouponUsePrice(order.getCouponUsePrice());
            hqSaleOrderDTO.setFinalTotalPrice(order.getFinalTotalPrice());
            hqSaleOrderDTO.setCharge(order.getCharge());
            hqSaleOrderDTO.setTotalOriginPrice(order.getTotalOriginPrice());
            hqSaleOrderDTO.setProfit(order.getProfit());
            list.add(hqSaleOrderDTO);
        }
        return list;
    }

    // file을 매개변수로 가지는 HttpHeaders 매서드
    public HttpHeaders makeHttpHeaders(File file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getName()).build());
        return headers;
    }

    //settlementDayList를 DTO에 넣기 위한 서비스
    public List<HqListForSaleDTO> makeHqListForSaleDTO(List<SettlementDay> list) {
        List<HqListForSaleDTO> DTOList = new ArrayList<>();
        for(SettlementDay settlementDay: list ) {
            HqListForSaleDTO hqListForSaleDTO = new HqListForSaleDTO();
            hqListForSaleDTO.setSettlementDate(settlementDay.getSettlementDate());
            Long storeId = settlementDay.getStore().getId(); // storeId가 아닌 settlementDayId 불러와서 논리적인 오류 발생
            Optional<Store> store = storeRepository.findById(storeId);
            String storeName = store.get().getName();
            hqListForSaleDTO.setStoreName(storeName);
            hqListForSaleDTO.setCharge(settlementDay.getCharge());
            hqListForSaleDTO.setSettlementPrice(settlementDay.getSettlementPrice());
            hqListForSaleDTO.setOriginPrice(settlementDay.getTotalOriginPrice());
            hqListForSaleDTO.setProfit(settlementDay.getProfit());
            DTOList.add(hqListForSaleDTO);
        }
        return DTOList;
    }
}