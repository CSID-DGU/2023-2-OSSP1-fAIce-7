package com.example.cokkiri.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@ToString
public class HobbyMatching {

    private int headCount;

    // 취미 목록
    private List<String> hobby;

    //매칭된 사람들 학번
    private String email;

    //매칭 가능한 시작 시간
    private LocalTime startTime;

    //매칭 가능한 끝 시간
    private LocalTime endTime;

    //매칭타입
    //공강=free , 수업=class, 취미=hobby
    private  String matchingType;
}
