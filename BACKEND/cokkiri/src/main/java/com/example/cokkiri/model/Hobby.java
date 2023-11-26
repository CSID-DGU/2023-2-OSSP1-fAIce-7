package com.example.cokkiri.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Hobby {

    @Id
    private String Id;

    // 취미 1 - 20
    @ColumnDefault("null")
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

    public List<String> getHobby() {
        ArrayList<String> hobbies = new ArrayList<>();

        if (hobby1 != null) hobbies.add(hobby1);
        if (hobby2 != null) hobbies.add(hobby2);
        if (hobby3 != null) hobbies.add(hobby3);
        if (hobby4 != null) hobbies.add(hobby4);
        if (hobby5 != null) hobbies.add(hobby5);
        if (hobby6 != null) hobbies.add(hobby6);
        if (hobby7 != null) hobbies.add(hobby7);
        if (hobby8 != null) hobbies.add(hobby8);
        if (hobby9 != null) hobbies.add(hobby9);
        if (hobby10 != null) hobbies.add(hobby10);

        return hobbies;
    }
}

