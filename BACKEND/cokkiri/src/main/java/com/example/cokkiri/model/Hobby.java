package com.example.cokkiri.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Hobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User에 있는 id와 join
    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "id")
    private User user;

    // 취미 1의 카테고리
    private String category1;

    // 취미 1
    private String hobby1;

    // 취미 2의 카테고리
    private String category2;

    // 취미 2
    private String hobby2;

    // 취미 3의 카테고리
    private String category3;

    // 취미 3
    @Column
    private String hobby3;
}
