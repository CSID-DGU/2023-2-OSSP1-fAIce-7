package com.example.cokkiri.service;

import com.example.cokkiri.model.Notification;
import com.example.cokkiri.repository.NotificationRepository;
import com.example.cokkiri.repository.NotificationRepositoryImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class SseService {

    private final NotificationRepositoryImpl emitterRepository;

    public SseEmitter subscribe(String email, String lastEventId) {

        String emitterId = makeTimeIncludeId(email);

        SseEmitter emitter;

        //버그 방지용으로 만든 코드입니다.
        if (emitterRepository.findAllEmitterStartWithByEmail(email) != null){
            emitterRepository.deleteAllEmitterStartWithId(email);
            emitter = emitterRepository.save(emitterId, new SseEmitter(Long.MAX_VALUE)); //id가 key, SseEmitter가 value
        }
        else {
            emitter = emitterRepository.save(emitterId, new SseEmitter(Long.MAX_VALUE)); //id가 key, SseEmitter가 value
        }

        //오류 종류별 구독 취소 처리
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId)); //네트워크 오류
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId)); //시간 초과
        emitter.onError((e) -> emitterRepository.deleteById(emitterId)); //오류

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(email);
        Notification dummy = createDummyNotification(email);
        sendNotification(emitter, eventId, emitterId, dummy);

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, email, emitterId, emitter);
        }

        return emitter;
    }

    //단순 알림 전송
    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        data = "{" + data + "}";
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(exception);
        }
    }

    private String makeTimeIncludeId(String email) { return email + "_" + System.currentTimeMillis(); }//Last-Event-ID의 값을 이용하여 유실된 데이터를 찾는데 필요한 시점을 파악하기 위한 형태

    //Last-Event-Id의 존재 여부 boolean 값
    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    //유실된 데이터 다시 전송
    private void sendLostData(String lastEventId, String email, String emitterId, SseEmitter emitter) {

        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByEmail(String.valueOf(email));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

//    sse연결 요청 응답
    /*-----------------------------------------------------------------------------------------------------------------------------------*/
//    서버에서 클라이언트로 일방적인 데이터 보내기

    //1ㄷ1로 특정 유저에게 알림 전송
    public void send(String receiver, String content, String type) {

        Notification notification = createNotifications(receiver, content, type);

        // 로그인 한 유저의 SseEmitter 모두 가져오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByEmail(receiver);

        sseEmitters.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key, notification);
                }
        );
    }
    //1ㄷ1로 List에 존재하는 특정 유저에게 알림 전송
    // param : emailList , "매칭이 성사되었습니다" , class or free
    public void sendList(List<String> receiverList, String content, String type) {

        List<Notification> notifications = new ArrayList<>();

        Map<String, SseEmitter> sseEmitters;

        for (int i = 0; i < receiverList.size(); i++) {

            int finalI = i;

            sseEmitters = new HashMap<>();

            notifications.add(createNotifications(receiverList.get(i), content, type));

            sseEmitters.putAll(emitterRepository.findAllEmitterStartWithByEmail(receiverList.get(i).toString()));

            sseEmitters.forEach(
                    (key, emitter) -> {
                        // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                        emitterRepository.saveEventCache(key, notifications.get(finalI));
                        // 데이터 전송
                        sendToClient(emitter, key, notifications.get(finalI));
                    }
            );
        }
    }


    private Notification createNotifications(String receiver, String content, String type) {

        if (type.equals("free")) {
            return Notification.builder()
                    .receiver(receiver)     // 사용자 이메일
                    .content(content)       // "매칭이 성사되었습니다"
                    .notificationType(type) // free
                    .isRead(false)          // 사용자가 읽었는지 판단
                    .notDummy(true)
                    .build();
        } else if (type.equals("class")) {
            return Notification.builder()
                    .receiver(receiver)     // 사용자 이메일
                    .content(content)
                    .notificationType(type)
                    .isRead(false)
                    .notDummy(true)
                    .build();
        }
        else {
            return null;
        }
    }


    //타입별 알림 생성
    private Notification createDummyNotification(String email) {
            return Notification.builder()
                    .receiver(email)
                    .content("this is dummy data")
                    .notificationType(null)
                    .isRead(false)
                    .notDummy(false)
                    .build();
    }


    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            sendNotification(emitter , id , id , data);
            emitterRepository.deleteById(id);

        } catch (Exception exception) {
            emitterRepository.deleteById(id);
            emitter.completeWithError(exception);

        }
    }
}
