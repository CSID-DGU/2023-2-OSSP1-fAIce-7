package com.example.cokkiri.service;

import com.example.cokkiri.model.Payment;
import com.example.cokkiri.model.User;
import com.example.cokkiri.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    //모든 결제내역 반환
    public List<Payment> findAll(){
        List<Payment> payments = new ArrayList<>();
        paymentRepository.findAll().forEach(e->payments.add(e));
        return payments;
    }

    //userId로 결제내역 반환
    public List<Payment> findById(String userId){
        return paymentRepository.findByUserId(userId);
    }

    //결제 내역 저장
    public Payment save(Payment payment){
        paymentRepository.save(payment);
        return payment;
    }
}
