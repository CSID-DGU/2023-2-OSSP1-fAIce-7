package com.example.cokkiri.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class NoShowPublicMatchList {
    @Id
    @Column
    private int matchingId;

    @Column
    private String email;

    @Column
    private  String matchingType;

    @Column
    private LocalDateTime restrictionTime;
}
