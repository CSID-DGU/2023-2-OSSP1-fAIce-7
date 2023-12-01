package com.example.cokkiri.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelMatching {

    //매칭된 사람들 학번
    private String studentId;

    @Builder.Default
    private boolean isCancel=false;

    //매칭타입
    //공강=free , 수업=class, 관심분야=hobby
    private  String matchingType;
}
