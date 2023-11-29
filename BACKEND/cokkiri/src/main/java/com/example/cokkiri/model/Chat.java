package com.example.cokkiri.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Chat {
    @Id
    @GeneratedValue
    @Column
    //채팅 아이디
    private int id;

    //채팅방 아이디
    @Column
    private int matchingId;

    @Column
    //매칭타입
    //공강=free , 수업=class
    private  String matchingType;

    //보낸 사람
    @Column
    private String sender;

    @Column
    private String content;

    //채팅 친 시간
    @CreationTimestamp
    @Column
    private LocalDateTime sendDate;
}
