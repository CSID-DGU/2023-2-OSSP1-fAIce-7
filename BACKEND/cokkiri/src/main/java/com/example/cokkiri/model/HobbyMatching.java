package com.example.cokkiri.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class HobbyMatching {

    private int headCount;

    // 매칭된 사람들 1순위 취미 목록
    private String hobby;

    //매칭된 사람들 학번
    private String email;

    //매칭타입
    //공강=free , 수업=class, 취미=hobby
    private  String matchingType;
}
