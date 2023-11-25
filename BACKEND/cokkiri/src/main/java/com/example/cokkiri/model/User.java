package com.example.cokkiri.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {

    //이메일
    @Id
    @Column
    private String id;
    //비밀번호
    @Column
    private String password;
    //이름
    @Column
    private String name;
    //성별
    @Column
    private String sex;
    //전공
    @Column
    private String major;
    //번호
    @Column
    private String number;
    //학번
    @Column
    private String studentNum;

    // 취미 1의 카테고리
    @Column
    private String category1;

    // 취미 1
    @Column
    private String hobby1;

    // 취미 2의 카테고리
    @Column
    private String category2;

    // 취미 2
    @Column
    private String hobby2;

    // 취미 3의 카테고리
    @Column
    private String category3;

    // 취미 3
    @Column
    private String hobby3;

    //수업 제한날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime restrctionDate;

    //수강과목
    @ElementCollection
    private List<String> course;
    //관리자
    @ColumnDefault("false")
    @Column
    private boolean admin;

    //인증여부
    @ColumnDefault("false")
    @Column
    private boolean auth;

    //인증키
    @Column
    private String authKey;

    //하트
    @Column
    @ColumnDefault("0")
    private int heart;
    
    //수업매칭중인지 확인 변수
    @Column
    @ColumnDefault("false")
    private boolean isClassMatching;

    //공강매칭중인지 확인 변수
    @Column
    @ColumnDefault("false")
    private boolean isPublicMatching;

    @Column
    @ColumnDefault("false")
    private boolean isHobbyMatching;

    // 카테고리 3개 설정 확인 변수
    @Column
    @ColumnDefault("false")
    private boolean isSetInterests;
}
