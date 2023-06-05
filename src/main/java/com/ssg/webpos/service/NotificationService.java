package com.ssg.webpos.service;

import com.ssg.webpos.controller.NotificationController;
import com.ssg.webpos.domain.Notification;
import com.ssg.webpos.domain.enums.NotificationType;
import com.ssg.webpos.dto.notification.NotificationDTO;
import com.ssg.webpos.dto.notification.NotificationResponseDTO;
import com.ssg.webpos.repository.notification.EmitterRepository;
import com.ssg.webpos.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
  private final EmitterRepository emitterRepository;
  private final NotificationRepository notificationRepository;

  // Last-Event-ID 헤더는 클라이언트가 마지막으로 수신한 데이터 id 값
  @Transactional
  public SseEmitter subscribe(Long hqId, String lastEventId, NotificationDTO notificationDTO) {
    String emitterId = makeTimeIncludeId(hqId);
    // 클라이언트의 sse 연결 요청에 응답하기 위해서 SseEmitter 객체를 만들어 반환
    // SseEmitter 객체를 만들 때 유효시간을 주어, 주는 시간만큼 sse 연결이 유지되고 시간이 지나면 자동으로 클라이언트에서 재연결 요청을 보냄
    SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
    // SseEmitter의 시간 초과 및 네트워크 오류를 포함한 모든 이유로 비동기 요청이 정상 동작할 수 없다면 저장해둔 SseEmitter를 삭제
    emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
    emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

    // 503 에러를 방지하기 위한 더미 이벤트 전송
    String eventId = makeTimeIncludeId(hqId);
//    sendNotification(emitter, eventId, emitterId, "EventStream Created. [hqId=" + hqId + "]");
    sendNotification(emitter, eventId, emitterId, notificationDTO);
    // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실 예방
    if (hasLostData(lastEventId)) {
      sendLostData(lastEventId, hqId, emitterId, emitter);
    }
//    emitter.complete();
    return emitter;
  }

  private String makeTimeIncludeId(Long hqId) {
    // 유실된 데이터 전송을 위함(id 값 그대로 사용하면 어떤 데이터까지 제대로 전송되었는지 알 수 없음)
    // 데이터가 유실된 시점을 파악하여 저장된 key 값 비교를 통해 유실된 데이터만 재전송
    return hqId + "_" + System.currentTimeMillis();
  }

  public void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
    try {
      emitter.send(SseEmitter.event()
          .id(eventId)
          .data(data));
    } catch (IOException exception) {
      emitterRepository.deleteById(emitterId);
    }
  }

  // lastEventId가 존재한다는 것은 받지 못한 데이터가 있다는 것
  private boolean hasLostData(String lastEventId) {
    return !lastEventId.isEmpty();
  }

  // 받지 못한 데이터가 있다면 lastEventId를 기준으로 그 뒤의 데이터를 추출해 알림을 보내줌
  private void sendLostData(String lastEventId, Long hqId, String emitterId, SseEmitter emitter) {
    Map<String, SseEmitter> eventCaches = emitterRepository.findAllEmitterStartWithByHqId(String.valueOf(hqId));
    System.out.println("eventCaches = " + eventCaches);
    eventCaches.entrySet().stream()
        .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
        .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
  }

  @Transactional
  public void send(NotificationDTO notificationDTO) {
    Notification notification = notificationRepository.save(createNotification(notificationDTO));
    System.out.println("notification = " + notification);

    String hqId = String.valueOf(notification.getId());
    String eventId = notificationDTO.getHqAdmin() + "_" + System.currentTimeMillis();
    Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByHqId(hqId);
    System.out.println("emitters = " + emitters);

    emitters.forEach((key, emitter) -> {
      emitterRepository.saveEventCache(key, notification);
      sendNotification(emitter, eventId, key, NotificationResponseDTO.create(notification));
    });
  }

  private Notification createNotification(NotificationDTO notificationDTO) {
    return Notification.builder()
        .hqAdmin(notificationDTO.getHqAdmin())
        .title(notificationDTO.getTitle())
        .content(notificationDTO.getContent())
        .isRead(notificationDTO.isRead())
        .notificationType(notificationDTO.getNotificationType())
        .build();
  }

  public SseEmitter connect(Long hqId) {
    SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
    try {
      sseEmitter.send(SseEmitter.event().name("connect").data("hqId=[" + hqId + "]"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    NotificationController.sseEmitters.put(hqId, sseEmitter);
    sseEmitter.onCompletion(() -> NotificationController.sseEmitters.remove(hqId));
    sseEmitter.onTimeout(() -> NotificationController.sseEmitters.remove(hqId));
    sseEmitter.onError((e) -> NotificationController.sseEmitters.remove(hqId));

    return sseEmitter;
  }

  @Transactional
  public void sendNoti(Long hqId) {
    NotificationDTO notificationDTO = NotificationDTO.builder()
        .notificationType(NotificationType.ORDER_REQUEST)
        .content("branch에서 발주 신청이 되었습니다.")
        .build();
    if(notificationDTO.getNotificationType().equals(NotificationType.ORDER_REQUEST)) {
      notificationDTO.setTitle("[발주 신청 알림]");
    }
    Notification notification = createNotification(notificationDTO);
    System.out.println("notification = " + notification);
    SseEmitter sseEmitter = NotificationController.sseEmitters.get(hqId);
    String notiMsg = notification.getTitle()
        + notification.getContent();
    try {
      sseEmitter.send(SseEmitter.event().name("notification").data(notiMsg));
//      sseEmitter.complete();
    } catch (Exception e) {
      NotificationController.sseEmitters.remove(hqId);
    }
  }
}
