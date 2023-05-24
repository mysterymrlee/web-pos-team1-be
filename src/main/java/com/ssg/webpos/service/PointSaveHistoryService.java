package com.ssg.webpos.service;

import com.ssg.webpos.domain.PointSaveHistory;
import com.ssg.webpos.domain.PointUseHistory;
import com.ssg.webpos.repository.PointSaveHistoryRepository;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointSaveHistoryService {
  private final PointSaveHistoryRepository pointSaveHistoryRepository;

  public void savePointSaveHistory(PointSaveHistory pointSaveHistory) {
    pointSaveHistoryRepository.save(pointSaveHistory);
  }

}
