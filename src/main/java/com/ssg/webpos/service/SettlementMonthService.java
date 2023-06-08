package com.ssg.webpos.service;

import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.dto.settlement.SettlementMonthReportDTO;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class SettlementMonthService {


}
