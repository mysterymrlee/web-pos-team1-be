package com.ssg.webpos.service;

import com.ssg.webpos.domain.PointUseHistory;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointUseHistoryService {
  private final PointUseHistoryRepository pointUseHistoryRepository;

  public void savePointUseHistory(PointUseHistory pointHistory) {
    pointUseHistoryRepository.save(pointHistory);
  }
}
