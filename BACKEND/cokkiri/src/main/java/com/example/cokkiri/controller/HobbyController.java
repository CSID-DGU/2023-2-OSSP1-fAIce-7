package com.example.cokkiri.controller;

import com.example.cokkiri.dto.UserHobbiesDTO;
import com.example.cokkiri.service.HobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HobbyController {

    @Autowired
    private HobbyService hobbyService;

    @PostMapping("/user/interests")
    public ResponseEntity<?> saveUserHobbies(@RequestBody UserHobbiesDTO userHobbies) {
        try {
            hobbyService.saveUserHobbies(userHobbies);
            return ResponseEntity.ok().body("Hobbies successfully saved for user: " + userHobbies.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving hobbies: " + e.getMessage());
        }
    }
}
