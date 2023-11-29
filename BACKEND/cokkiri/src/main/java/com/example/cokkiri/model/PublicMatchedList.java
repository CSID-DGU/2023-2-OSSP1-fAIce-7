package com.example.cokkiri.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PublicMatchedList {
    //매칭 결과에 대한 Entity

    //매칭 번호 (auto increment)
    @Id
    @GeneratedValue
    @Column
    private int matchingId;

    //희망인원
    @Column
    private int headCount;

    //매칭된 시간
    @Column
    private LocalDate matchingTime;

    //매칭된 사람들 이메일
    @ElementCollection
    @Column
    private List<String> emailList;

    // 매칭 완료 누르면 이메일 주소 이동
    @ElementCollection
    @Column
    private List<String> agreeList;

    @Column
    //매칭 가능한 요일
    private  LocalDate availableDay;

    //약속시간
    @Column
    @ElementCollection
    private List<LocalTime> promiseTime;

    @Column
    //매칭 동의수 사람수랑 같게 되면 matchingRes=매칭중
    private int matchingAgree;

    @Column
    //매칭결과
    private String matchingRes;

    @Column
    //매칭타입
    //공강=free , 수업=class
    private  String matchingType;

}

