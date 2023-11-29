package com.example.cokkiri.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ClassMatchedList {
    //매칭 결과에 대한 Entity

    //매칭 번호 (auto increment)
    @Id
    @GeneratedValue
    @Column
    private int matchingId;

    //학수번호
    @Column
    @ElementCollection
    private List<String> courseNumber;

    //희망인원
    @Column
    private int headCount;

    //매칭된 시간
    @Column
    private LocalDate matchingTime;

    //매칭된 사람들 이메일
    @ElementCollection
    private List<String> emailList;

    // 매칭 완료 누르면 이메일 주소 이동
    @ElementCollection
    @Column
    private List<String> agreeList;

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

