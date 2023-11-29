package com.example.cokkiri.repository;

import com.example.cokkiri.model.MatchingAgree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchingAgreeRepository extends JpaRepository<MatchingAgree, Integer> {
    public List<MatchingAgree> findByEmailAndMatchingType(String email , String matchingType);

}
