package com.example.cokkiri.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserHobbiesDTO {
    private String id;
    private List<HobbyDTO> interests;

    public UserHobbiesDTO() {
        // Initialize the hobbies list in the constructor
        this.interests = new ArrayList<>();
    }

    public void setHobbies(List<HobbyDTO> hobbies) {
        this.interests = hobbies;
    }
}
