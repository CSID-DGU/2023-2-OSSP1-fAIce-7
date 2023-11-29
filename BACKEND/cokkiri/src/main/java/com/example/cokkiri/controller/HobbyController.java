package com.example.cokkiri.controller;

import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.model.User;
import com.example.cokkiri.repository.HobbyRepository;
import com.example.cokkiri.repository.UserRepository;
import com.example.cokkiri.service.HobbyService;
import com.example.cokkiri.utils.HobbyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class HobbyController {

    @Autowired
    private HobbyService hobbyService;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/api/interests/save/")
    public ResponseEntity<Map<String, Object>> saveHobby(@RequestBody HobbyRequest hobbyRequest) {
        // 요청 받은 데이터 로그
        System.out.println("Received hobbyRequest: " + hobbyRequest);
        String userId = hobbyRequest.getUserId();
        Map<String, String> interests = hobbyRequest.getInterests();

        // 처리 전 userId와 interests 로그
        System.out.println("Processing hobbyRequest with userId: " + userId + " and interests: " + interests);
        Hobby savedHobby = hobbyService.saveHobby(userId, interests);

        // 저장된 Hobby 객체 로그
        System.out.println("Saved Hobby: " + savedHobby);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("interests", interests);

        // 응답 데이터 로그
        System.out.println("Response data: " + response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/user/interests")
    public ResponseEntity<Hobby> getUserHobby(@RequestParam(value="userId") String userId) {
        System.out.println("받은 요청: 사용자 ID - " + userId);
        Optional<Hobby> hobby = hobbyService.getUserHobbies(userId);
        System.out.println("보내는 응답: " + hobby);
        return hobby.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}

