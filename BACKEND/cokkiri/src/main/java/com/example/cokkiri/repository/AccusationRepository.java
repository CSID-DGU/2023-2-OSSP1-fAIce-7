package com.example.cokkiri.repository;

import com.example.cokkiri.model.ClassAccusation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccusationRepository extends JpaRepository<ClassAccusation,String> {
    public List<ClassAccusation> findByMatchingType(String matchingType);
    public  List<ClassAccusation> findByMatchingIdAndMatchingType(int id, String matchingType);
    Optional<ClassAccusation> findByMatchingIdAndEmail(int matchingId , String id);
}
