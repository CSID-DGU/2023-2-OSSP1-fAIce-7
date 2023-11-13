package com.example.cokkiri.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification {

    @Id
    private String receiver; //알림을 받는 유저의 정보 , 이메일

    @Column
    private String notificationType; //알림 종류별 분류를 위한

    @Column
    private String content; //알람의 내용 -> "매칭이 성사 되었습니다 !"


    @Column
    private Boolean isRead; //알림 열람에 대한 여부
    
    @Column
    private  Boolean notDummy; // 더미 데이터 여부
}
