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
    @ColumnDefault("10000")
    private int heart;
    
    //수업매칭중인지 확인 변수
    @Column
    @ColumnDefault("false")
    private boolean isClassMatching;

    //공강매칭중인지 확인 변수
    @Column
    @ColumnDefault("false")
    private boolean isPublicMatching;
}
