package com.ssg.webpos.controller.admin;

import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hq")
@Slf4j
@RequiredArgsConstructor
public class HQSettlementController {
    private final SettlementDayRepository settlementDayRepository;
    private final SettlementMonthRepository settlementMonthRepository;
}
