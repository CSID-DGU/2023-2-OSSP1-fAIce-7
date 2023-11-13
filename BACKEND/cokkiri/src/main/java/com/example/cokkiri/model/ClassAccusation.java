package com.example.cokkiri.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ClassAccusation {
    @Column
    private int matchingId; // 신고할 매치의 id

    @Id
    @Column
    private String email; // 신고자

    @Column
    private String title; // 신고내용 제목

    @Column
    private String comment; // 신고 내용

    @Column
    private String matchingType; // 신고 발생 matching type

}
