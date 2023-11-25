package com.example.cokkiri.utils;

import lombok.Data;

@Data
public class pairIdScore {
    String id;
    float score;

    public pairIdScore(String id, float score) {
        this.id = id;
        this.score = score;
    }
}
