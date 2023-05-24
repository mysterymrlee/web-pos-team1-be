package com.ssg.webpos.controller.admin;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.ProductRequest;
import com.ssg.webpos.domain.Store;
import com.ssg.webpos.dto.stock.stockSubmit.*;
import com.ssg.webpos.repository.ProductRequestRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import com.ssg.webpos.service.SettlementDayService;
import com.ssg.webpos.service.SettlementMonthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/branchadmin-staff")
@Slf4j
@RequiredArgsConstructor
public class BranchAdminStaffController {
    // staff 기능 : 재고 조회, 수정, 삭제, 재고 리포트(주말 재고 현황) 제출
    // 리포트가 이미 제출된 경우 버튼을 비활성화

    // 재고 신청해야 하는 상품의 수량을 입력한 DTO를 받으면 request_product 테이블에 넣을 DTO를 리턴한다.
    private final ProductRequestRepository productRequestRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    @PostMapping("/stock-report/submit")
    public ResponseEntity stockReportSubmit(@RequestBody SubmitRequestDTOList submitRequestDTOList) {
        try {
            List<SubmitRequestDTO> submitRequestDTOs = submitRequestDTOList.getSubmitRequestDTOList();
            // REST API에서 활용할 DTO 작성 시작
            SubmitResponseDTOList submitResponseDTOList = new SubmitResponseDTOList();
            submitResponseDTOList.setStoreId(submitRequestDTOList.getStoreId());
            List<SubmitResponseDTO> submitResponseDTOs = new ArrayList<>(); // 리스트 형식
            for(SubmitRequestDTO submitRequestDTO:submitRequestDTOs) {
                SubmitResponseDTO submitResponseDTO = new SubmitResponseDTO();
                submitResponseDTO.setProductRequestId(submitRequestDTO.getStockReportId());
                submitRequestDTO.setQty(submitRequestDTO.getQty());
                submitResponseDTO.setProductId(submitRequestDTO.getProductId());
                submitResponseDTO.setCurrentStock(submitRequestDTO.getCurrentStock());
                submitResponseDTO.setCreateTime(LocalDateTime.now());
                submitResponseDTO.setLastModifiedTime(LocalDateTime.now()); // 클라이언트에서 요청이 들어온 시간을 가져오는 게 아님.. 서버에서 해당 메서드가 호출된 시간을 사용하여 필드 값을 설정
                submitResponseDTOs.add(submitResponseDTO);
            }
            submitResponseDTOList.setSubmitResponseDTO(submitResponseDTOs); // 이제 이 DTO를 product_request 에 JpaRepository를 활용해 입력하고 싶다.
            // REST API에서 활용할 DTO 작성 종료
            // 테이블에 입력할 정보 따로, POST REST API에서 활용할 것 따로 작성했습니다.
            // 테이블에는 store_id를 포함한 열이 필요하고 POST REST API에서는 store_id와 다른 칼럼들을 나누어서 사용하기 때문에
            // DTO 를 사용하면 테이블에 넣기 어렵고 엔티티 사용하면 연관관계때문에 Lazy에러 발생할 것 같고..
            List<ProductRequest> productRequests = new ArrayList<>(); // DTO를 엔티티로 변환한 다음 테이블에 입력할 것이다.
            for(SubmitRequestDTO submitRequestDTO:submitRequestDTOs) {
                ProductRequest productRequest = new ProductRequest();
                productRequest.setQty(submitRequestDTO.getQty());
//                Product product = productRepository.findById(submitRequestDTO.getProductId()); // 팀장님 ProductRepository 수정해도 되는지
                Optional<Product> findProduct = productRepository.findById(submitRequestDTO.getProductId());
                productRequest.setProduct(findProduct.get()); // 양방향 연관관계 필요없음(product에서 product_request 조회x)
                Optional<Store> store = storeRepository.findById(submitRequestDTOList.getStoreId());
//                productRequest.setStore(store.get());
                productRequest.addStoreWithAssociation(store.get());
            }


            return new ResponseEntity(submitResponseDTOList,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }


}
