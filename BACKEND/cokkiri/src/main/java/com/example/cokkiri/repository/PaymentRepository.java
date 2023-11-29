package com.example.cokkiri.repository;

import com.example.cokkiri.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    public List<Payment> findByUserId(String userId);
}
