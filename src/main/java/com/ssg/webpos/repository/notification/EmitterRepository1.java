package com.ssg.webpos.repository.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class EmitterRepository1 {
  private Map<String, SseEmitter> emitterMap = new HashMap<>();

  public SseEmitter save(Long hqId, SseEmitter sseEmitter) {
    emitterMap.put(getKey(hqId), sseEmitter);
    log.info("Saved SseEmitter for {}", hqId);
    return sseEmitter;
  }

  public Optional<SseEmitter> get(Long hqId) {
    log.info("Got SseEmitter for {}", hqId);
    return Optional.ofNullable(emitterMap.get(getKey(hqId)));
  }

  public void delete(Long hqId) {
    emitterMap.remove(getKey(hqId));
    log.info("Deleted SseEmitter for {}", hqId);
  }

  private String getKey(Long hqId) {
    return "Emitter:UID: " + hqId;
  }
}
