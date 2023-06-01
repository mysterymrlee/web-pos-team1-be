package com.ssg.webpos.service;

import com.ssg.webpos.domain.PointSaveHistory;
import com.ssg.webpos.domain.PointUseHistory;
import com.ssg.webpos.repository.PointSaveHistoryRepository;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointSaveHistoryService {
  private final PointSaveHistoryRepository pointSaveHistoryRepository;

  public void savePointSaveHistory(PointSaveHistory pointSaveHistory) {
    pointSaveHistoryRepository.save(pointSaveHistory);
  }
  public void deleteExpiredPoints() {
    LocalDateTime currentDate = LocalDateTime.now();
    List<PointSaveHistory> expiredPoints = pointSaveHistoryRepository.findByExpiredDateBefore(currentDate);
    pointSaveHistoryRepository.deleteAll(expiredPoints);
  }

}
