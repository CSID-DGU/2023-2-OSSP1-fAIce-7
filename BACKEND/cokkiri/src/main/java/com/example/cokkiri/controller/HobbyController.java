package com.example.cokkiri.controller;

import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.repository.UserRepository;
import com.example.cokkiri.service.HobbyService;
import com.example.cokkiri.utils.HobbyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class HobbyController {

    @Autowired
    private HobbyService hobbyService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/api/interests/{userId}")
    public ResponseEntity<Optional<Hobby>> getUserHobbies(@PathVariable String userId) {
        Optional<Hobby> hobbies = hobbyService.getUserHobbies(userId);
        return ResponseEntity.ok(hobbies);
    }

    @PostMapping("/api/interests/save")
    public ResponseEntity<Map<String, Object>> saveHobby(@RequestBody HobbyRequest hobbyRequest) {

        String userId = hobbyRequest.getUserId();
        Map<String, String> interests = hobbyRequest.getInterests();


        Hobby savedHobby = hobbyService.saveHobby(userId, interests);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("interests", interests);

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = {"api/user/interests"})
    public ResponseEntity<Hobby> getUserHobby(@RequestParam(value="userId")String id){

        //관리자페이지에서 이메일으로 유저의 취미 조회 (마이페이지에서도 이걸 사용할 것)
        Optional<Hobby> hobby = hobbyService.getUserHobbies(id);
        return hobby.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}

