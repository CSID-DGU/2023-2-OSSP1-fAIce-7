package com.example.cokkiri.model;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Data
@Builder
@ToString
public class ClassMatching {


    //매칭된 사람수
    private int headCount;

    //매칭된 사람들 학번
    private String email;

    //학수번호
    private List<String> courseNumber;

    //매칭타입
    //공강=free , 수업=class
    private  String matchingType;

}