package com.example.cokkiri.controller;

import com.example.cokkiri.model.Chat;
import com.example.cokkiri.model.PublicMatchedList;
import com.example.cokkiri.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    @Autowired
    private ChatService chatService;

    @MessageMapping("/{matchingId}/{matchingType}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{matchingId}/{matchingType}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public Chat chat(Message<Chat> m) {
        //log.info(m.toString());
        Chat chat = Chat.builder()
                .matchingId(m.getPayload().getMatchingId())
                .matchingType(m.getPayload().getMatchingType())
                .sender(m.getPayload().getSender())
                .content(m.getPayload().getContent())
                .build();

        return chatService.save(chat);
    }

    @GetMapping("/room/{matchingId}/{matchingType}")
    public ResponseEntity<List<Chat>> loadChat(@PathVariable int matchingId,@PathVariable String matchingType){
        List<Chat> chats = chatService.findAllChatByRoomId(matchingId,matchingType);
        return new ResponseEntity<List<Chat>>(chats, HttpStatus.OK);
    }
}