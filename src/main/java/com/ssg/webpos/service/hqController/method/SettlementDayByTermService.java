package com.ssg.webpos.service.hqController.method;

import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.dto.hqSale.HqSettlementDayDTO;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementDayByTermService {
    private final SettlementDayRepository settlementDayRepository;

    public List<HqSettlementDayDTO> HqSaleMethods(List<SettlementDay> settlementDayList) {
        List<HqSettlementDayDTO> hqSettlementDayDTOList = new ArrayList<>();
        for(SettlementDay settlementDay : settlementDayList ) {
            HqSettlementDayDTO hqSettlementDayDTO = new HqSettlementDayDTO();
            hqSettlementDayDTO.setSettlementDayDate(settlementDay.getSettlementDate());
            hqSettlementDayDTO.setSettlementDaySettlementPrice(settlementDay.getSettlementPrice());
            hqSettlementDayDTOList.add(hqSettlementDayDTO);
        }
        return hqSettlementDayDTOList;
    }

}
