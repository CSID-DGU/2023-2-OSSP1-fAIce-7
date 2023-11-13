package com.example.cokkiri.service;

import com.example.cokkiri.model.Chat;
import com.example.cokkiri.model.User;
import com.example.cokkiri.repository.ChatRepository;
import com.example.cokkiri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    public Chat save(Chat chat){
        //여기서 실제로 있는 채팅방인가에 대해서 Check 해줘야 하고 채팅 저장.
        chatRepository.save(chat);
        return chat;
    }

    //채팅방 내용 불러오기
    public List<Chat> findAllChatByRoomId(int id,String matchingType){
        return chatRepository.findAllByAndMatchingIdAndMatchingType(id,matchingType);
    }
}
