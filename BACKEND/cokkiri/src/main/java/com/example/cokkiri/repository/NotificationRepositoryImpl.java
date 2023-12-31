package com.example.cokkiri.repository;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@NoArgsConstructor
@Log4j2
public class NotificationRepositoryImpl implements NotificationRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        log.info(emitters);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        if (event != null) {
            eventCache.put(eventCacheId, event);
        } else {
            // 이벤트가 null인 경우 예외 처리 또는 다른 처리를 수행해야 합니다.
            log.error("Event is null for eventCacheId: " + eventCacheId);
        }
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByEmail(String email) {
        return emitters.entrySet().stream() //여러개의 Emitter가 존재할 수 있기떄문에 stream 사용
                .filter(entry -> entry.getKey().startsWith(email))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByEmailInList(List emails) {
        return null;
    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithByEmail(String email) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(email))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(String id) {
        emitters.remove(id);
    }

    @Override
    public void deleteAllEmitterStartWithId(String email) {
        emitters.forEach((key, emitter) -> {
            if (key.startsWith(email)){
                emitters.remove(key);
            }
        });
    }

    @Override
    public void deleteAllEventCacheStartWithId(String email) {
        emitters.forEach((key, emitter) -> {
            if (key.startsWith(email)){
                emitters.remove(key);
            }
        });
    }
}
