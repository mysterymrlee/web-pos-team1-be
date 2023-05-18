package com.ssg.webpos.controller.admin;

import com.ssg.webpos.service.SettlementDayService;
import com.ssg.webpos.service.SettlementMonthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/branchadmin-staff")
@Slf4j
@RequiredArgsConstructor
public class BranchAdminStaffController {
    // staff 기능 : 재고 조회, 수정, 삭제, 재고 리포트(주말 재고 현황) 제출
    // 리포트가 이미 제출된 경우 버튼을 비활성화
    private final SettlementDayService settlementDayService;
    private final SettlementMonthService settlementMonthService;
}
