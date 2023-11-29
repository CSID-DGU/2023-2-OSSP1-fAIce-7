package com.example.cokkiri.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class HobbyMatchingWait {

    @Id
    @GeneratedValue
    private int id; // 번호

    @Column
    private String email; // 사용자 이메일

    @Column
    private String matchingType; // 매칭타입

    @Column
    private String status; // 매칭 대기

    //매칭 신청한 시간
    @Column
    private LocalDate matchingTime;
}
