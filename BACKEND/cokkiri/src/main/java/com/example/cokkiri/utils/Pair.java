package com.example.cokkiri.utils;

public class Pair implements Comparable<Pair> {
    private String id;
    private Double score;

    public Pair(String id, Double score) {
        this.id = id;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public Double getScore() {
        return score;
    }

    public boolean isEqual(Pair a, Pair b) {
        return a.getId() == b.getId() && a.getScore() == b.getScore();
    }

    @Override
    public int compareTo(Pair other) {
        // score를 기준으로 오름차순으로 정렬
        return Double.compare(this.score, other.score);
    }
}
