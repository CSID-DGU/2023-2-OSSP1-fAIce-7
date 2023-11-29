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

    @Autowired
    HobbyRepository hobbyRepository;

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
    public ResponseEntity<List<String>> getUserHobby(@RequestParam(value="userId")String id){

        // 존재하는 유저?
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else {
            // 유저가 적어도 한번 설정했는지 확인
            Optional<Hobby> hobbies = hobbyService.getUserHobbies(id);

            if (hobbies.isPresent()) {

                List<String> hobby = hobbies.get().getHobby();

                return new ResponseEntity<>(hobby, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
    }
}

