package com.example.cokkiri.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Hobby {

    // User에 있는 id와 join
    @Id
    @Column
    private String id;

    // 취미 1 - 20
    private String hobby1;
    private String hobby2;
    private String hobby3;
    private String hobby4;
    private String hobby5;
    private String hobby6;
    private String hobby7;
    private String hobby8;
    private String hobby9;
    private String hobby10;

    public void setHobby(int hobbyNumber, String name) {
        switch (hobbyNumber) {
            case 1 -> this.hobby1 = name;
            case 2 -> this.hobby2 = name;
            case 3 -> this.hobby3 = name;
            case 4 -> this.hobby4 = name;
            case 5 -> this.hobby5 = name;
            case 6 -> this.hobby6 = name;
            case 7 -> this.hobby7 = name;
            case 8 -> this.hobby8 = name;
            case 9 -> this.hobby9 = name;
            case 10 -> this.hobby10 = name;
            default -> throw new IllegalArgumentException("Invalid hobby number");
        }
    }
}

