package com.ssg.webpos.service;

import com.ssg.webpos.repository.notification.EmitterRepository1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService1 {
  private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간
  private final static String NOTIFICATION_NAME = "notify";
  private final EmitterRepository1 emitterRepository;

  public SseEmitter connectNotification(Long hqId) {
    // 새로운 SseEmitter를 만듦
    SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
    // hqId로 SseEmitter를 저장
    emitterRepository.save(hqId, sseEmitter);
    // 세션이 종료될 경우 저장한 SseEmitter를 삭제
    sseEmitter.onCompletion(() -> emitterRepository.delete(hqId));
    sseEmitter.onTimeout(() -> emitterRepository.delete(hqId));

    // 503 Service Unavailable 오류가 발생하지 않도록 첫 데이터를 보냄
    try {
      sseEmitter.send(SseEmitter.event()
          .id("")
          .name(NOTIFICATION_NAME)
          .data("Connection completed"));
      System.out.println("send complete");
    } catch (IOException exception) {
      throw new RuntimeException("연결 오류!");
    }
    return sseEmitter;
  }

  public void send(Long hqId, Long notificationId) {
    // hqId로 SseEmitter를 찾아 이벤트 발생 시킴
    emitterRepository.get(hqId).ifPresentOrElse(sseEmitter -> {
      try {
        sseEmitter.send(SseEmitter.event()
            .id(notificationId.toString())
            .name(NOTIFICATION_NAME)
            .data("New notification"));
        System.out.println("new notification");
      } catch (IOException exception) {
        // IOException이 발생하면 저장된 SseEmitter를 삭제하고 예외 발생시킴
        emitterRepository.delete(hqId);
        throw new RuntimeException("연결 오류!");
      }
    }, () -> log.info("No emitter found"));
  }
}
