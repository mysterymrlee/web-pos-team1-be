package com.ssg.webpos.repository.notification;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
  SseEmitter save(String emitterId, SseEmitter sseEmitter);
  void saveEventCache(String emitterId, Object event);
  // 해당 hqAdmin과 관련된 모든 emitter를 찾음
  Map<String, SseEmitter> findAllEmitterStartWithByHqId(String hqId);
  // 해당 hqAdmin과 관련된 모든 event를 찾음
  Map<String, Object> findAllEventCacheStartWithByHqId(String hqId);

  void deleteById(String emitterId);
}
