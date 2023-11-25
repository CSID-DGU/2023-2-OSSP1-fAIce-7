package com.example.cokkiri.service;

import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.repository.HobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HobbyService {

    @Autowired
    private HobbyRepository hobbyRepository;

    public List<Hobby> getUserHobbies(Long userId) {
        // userId를 기반으로 사용자의 취미 목록을 조회
        return hobbyRepository.findByUser_Id(userId);
    }

    public Hobby saveHobby(Hobby hobby) {
        // 취미 저장
        return hobbyRepository.save(hobby);
    }
}
