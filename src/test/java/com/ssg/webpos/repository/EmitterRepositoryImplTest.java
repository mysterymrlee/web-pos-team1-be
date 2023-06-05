package com.ssg.webpos.repository;

import com.ssg.webpos.domain.HQAdmin;
import com.ssg.webpos.domain.Notification;
import com.ssg.webpos.domain.enums.NotificationType;
import com.ssg.webpos.repository.notification.EmitterRepository;
import com.ssg.webpos.repository.notification.EmitterRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.util.Map;

@SpringBootTest
@Transactional
public class EmitterRepositoryImplTest {
  @Autowired
  private HQAdminRepository hqAdminRepository;
  private EmitterRepository emitterRepository = new EmitterRepositoryImpl();
  private Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;

  @Test
  @DisplayName("새로운 Emitter 추가")
  public void save() {
    // given
    Long hqId = 1L;
    String emitterId = hqId + "_" + System.currentTimeMillis();
    System.out.println("emitterId = " + emitterId);
    SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

    Assertions.assertDoesNotThrow(() -> emitterRepository.save(emitterId, sseEmitter));
  }

  @Test
  @DisplayName("수신한 이벤트를 캐시에 저장")
  public void saveEventCache() {
    Long hqId = 1L;
    String eventCacheId = hqId + "_" + System.currentTimeMillis();
    HQAdmin hqAdmin = hqAdminRepository.findById(hqId).get();

    Notification notification = Notification.builder()
        .title("발주 신청")
        .hqAdmin(hqAdmin)
        .content("테스트입니다.")
        .isRead(false)
        .notificationType(NotificationType.ORDER_REQUEST)
        .build();
    System.out.println("notification = " + notification);
    Assertions.assertDoesNotThrow(() -> emitterRepository.saveEventCache(eventCacheId, notification));
  }

  @Test
  @DisplayName("어떤 회원이 접속한 모든 Emitter를 찾는다")
  public void findAllEmitterStartWithByHqId() throws InterruptedException {
    Long hqId = 1L;
    String emitterId1 = hqId + "_" + System.currentTimeMillis();
    emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

    Thread.sleep(100);
    String emitterId2 = hqId + "_" + System.currentTimeMillis();
    emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

    Thread.sleep(100);
    String emitterId3 = hqId + "_" + System.currentTimeMillis();
    emitterRepository.save(emitterId3, new SseEmitter(DEFAULT_TIMEOUT));

    Map<String, SseEmitter> ActualResult = emitterRepository.findAllEmitterStartWithByHqId(String.valueOf(hqId));
    System.out.println("ActualResult = " + ActualResult);

    Assertions.assertEquals(3, ActualResult.size());
  }
}
