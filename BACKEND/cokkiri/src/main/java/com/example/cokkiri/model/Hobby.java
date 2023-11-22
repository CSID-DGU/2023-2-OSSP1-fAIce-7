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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User에 있는 id와 join
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // 취미 1 - 20
    private Integer hobby1;
    private Integer hobby2;
    private Integer hobby3;
    private Integer hobby4;
    private Integer hobby5;
    private Integer hobby6;
    private Integer hobby7;
    private Integer hobby8;
    private Integer hobby9;
    private Integer hobby10;
    private Integer hobby11;
    private Integer hobby12;
    private Integer hobby13;
    private Integer hobby14;
    private Integer hobby15;
    private Integer hobby16;
    private Integer hobby17;
    private Integer hobby18;
    private Integer hobby19;
    private Integer hobby20;
    private Integer hobby21;
    private Integer hobby22;
    private Integer hobby23;
    private Integer hobby24;


    public int getHobbyScore(int hobbyNumber) {
        switch (hobbyNumber) {
            case 1 -> {
                return this.hobby1;
            }
            case 2 -> {
                return this.hobby2;
            }
            case 3 -> {
                return this.hobby3;
            }
            case 4 -> {
                return this.hobby4;
            }
            case 5 -> {
                return this.hobby5;
            }
            case 6 -> {
                return this.hobby6;
            }
            case 7 -> {
                return this.hobby7;
            }
            case 8 -> {
                return this.hobby8;
            }
            case 9 -> {
                return this.hobby9;
            }
            case 10 -> {
                return this.hobby10;
            }
            case 11 -> {
                return this.hobby11;
            }
            case 12 -> {
                return this.hobby12;
            }
            case 13 -> {
                return this.hobby13;
            }
            case 14 -> {
                return this.hobby14;
            }
            case 15 -> {
                return this.hobby15;
            }
            case 16 -> {
                return this.hobby16;
            }
            case 17 -> {
                return this.hobby17;
            }
            case 18 -> {
                return this.hobby18;
            }
            case 19 -> {
                return this.hobby19;
            }
            case 20 -> {
                return this.hobby20;
            }
            case 21 -> {
                return this.hobby21;
            }
            case 22 -> {
                return this.hobby22;
            }
            case 23 -> {
                return this.hobby23;
            }
            case 24 -> {
                return this.hobby24;
            }
            default -> throw new IllegalArgumentException("Invalid hobby number");
        }
    }

    public void setHobby(int hobbyNumber, int score) {
        switch (hobbyNumber) {
            case 1 -> this.hobby1 = score;
            case 2 -> this.hobby2 = score;
            case 3 -> this.hobby3 = score;
            case 4 -> this.hobby4 = score;
            case 5 -> this.hobby5 = score;
            case 6 -> this.hobby6 = score;
            case 7 -> this.hobby7 = score;
            case 8 -> this.hobby8 = score;
            case 9 -> this.hobby9 = score;
            case 10 -> this.hobby10 = score;
            case 11 -> this.hobby11 = score;
            case 12 -> this.hobby12 = score;
            case 13 -> this.hobby13 = score;
            case 14 -> this.hobby14 = score;
            case 15 -> this.hobby15 = score;
            case 16 -> this.hobby16 = score;
            case 17 -> this.hobby17 = score;
            case 18 -> this.hobby18 = score;
            case 19 -> this.hobby19 = score;
            case 20 -> this.hobby20 = score;
            case 21 -> this.hobby21 = score;
            case 22 -> this.hobby22 = score;
            case 23 -> this.hobby23 = score;
            case 24 -> this.hobby24 = score;
            default -> throw new IllegalArgumentException("Invalid hobby number");
        }
    }

    public String getUserId() {
        return this.user.getId(); // User 객체의 getEmail 메소드를 호출
    }
}

