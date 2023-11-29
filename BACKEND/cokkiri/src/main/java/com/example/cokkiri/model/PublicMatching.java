package com.example.cokkiri.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@ToString
public class PublicMatching {


    //매칭된 사람수
    private int headCount;

    //매칭된 사람들 학번
    private String email;

    //매칭 가능한 요일
    private  LocalDate availableDay;

    //매칭 가능한 시작 시간
    private LocalTime startTime;

    //매칭 가능한 끝 시간
    private LocalTime endTime;

    //매칭타입
    //공강=free , 수업=class
    private  String matchingType;


}