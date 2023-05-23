package com.ssg.webpos.service;

import com.ssg.webpos.domain.PointUseHistory;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
  private final PointUseHistoryRepository pointUseHistoryRepository;

  public void savePointHistory(PointUseHistory pointHistory) {
    pointUseHistoryRepository.save(pointHistory);
  }
}
