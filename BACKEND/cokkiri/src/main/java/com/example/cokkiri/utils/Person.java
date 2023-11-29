package com.example.cokkiri.utils;

class Person {
    private String name;
    private int[] preferences;
    private Person currentMatch; // 상대방과의 매칭 정보 추가

    public Person(String name, int[] preferences) {
        this.name = name;
        this.preferences = preferences;
        this.currentMatch = null; // 초기값은 아무도와 매칭되지 않음
    }

    public String getName() {
        return name;
    }

    public int[] getPreferences() {
        return preferences;
    }

    public Person getCurrentMatch() {
        return currentMatch;
    }

    public void setCurrentMatch(Person partner) {
        this.currentMatch = partner;
    }
}
