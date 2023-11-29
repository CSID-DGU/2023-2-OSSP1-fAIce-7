package com.example.cokkiri.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Payment {
    //거래내역 entity

    //거래 id
    @Id
    @GeneratedValue
    @Column
    private int id;

    //결제 한 사람
    @Column
    private String userId;

    //결제 일시
    @Column
    private Date payDate;

    //가격
    @Column
    private int amount;

}
