package com.example.cokkiri.dto;

import com.microsoft.schemas.office.office.STInsetMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HobbyDTO {

    private String category;
    private String item;
    private int score;

}
