package com.example.cokkiri.controller;

import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.service.HobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interests")
public class HobbyController {

    @Autowired
    private HobbyService hobbyService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Hobby>> getUserHobbies(@PathVariable String userId) {
        List<Hobby> hobbies = hobbyService.getUserHobbies(userId);
        return ResponseEntity.ok(hobbies);
    }

    @PostMapping
    public ResponseEntity<Hobby> saveHobby(@RequestBody Hobby hobby) {
        Hobby savedHobby = hobbyService.saveHobby(hobby);
        return ResponseEntity.ok(savedHobby);
    }
}

