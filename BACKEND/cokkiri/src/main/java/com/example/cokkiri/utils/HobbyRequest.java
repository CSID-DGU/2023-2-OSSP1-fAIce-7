package com.example.cokkiri.utils;

import java.util.Map;

public class HobbyRequest {
    private String userId;
    private Map<String, String> interests;

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, String> getInterests() {
        return interests;
    }

    public void setInterests(Map<String, String> interests) {
        this.interests = interests;
    }
}
