package com.ssg.webpos.repository.notification;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {
  // 동시성을 고려하여 ConcurrentHashmap 사용
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final Map<String, Object> eventCache = new ConcurrentHashMap<>();
  @Override
  public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
    emitters.put(emitterId, sseEmitter);
    return sseEmitter;
  }

  @Override
  public void saveEventCache(String eventCacheId, Object event) {
    eventCache.put(eventCacheId, event);
  }

  @Override
  public Map<String, SseEmitter> findAllEmitterStartWithByHqId(String hqId) {
    return emitters.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(hqId))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public Map<String, Object> findAllEventCacheStartWithByHqId(String hqId) {
    return eventCache.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(hqId))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public void deleteById(String id) {
    emitters.remove(id);
  }
}
