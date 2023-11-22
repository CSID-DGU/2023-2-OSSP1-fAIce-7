package com.example.cokkiri.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserHobbiesDTO {
    private String id;
    private List<HobbyDTO> hobbies;
}
