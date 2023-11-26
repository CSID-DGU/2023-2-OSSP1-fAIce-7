package com.example.cokkiri.repository;

import com.example.cokkiri.model.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface HobbyRepository extends JpaRepository<Hobby, String> {
    List<Hobby> findByUser_Id(String userId);
}
