package com.example.cokkiri.repository;

import com.example.cokkiri.model.PublicAccusation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublicAccusationRepository extends JpaRepository<PublicAccusation,String> {
    public List<PublicAccusation> findByMatchingType(String matchingType);
    public List<PublicAccusation> findByMatchingIdAndMatchingType(int id, String matchingType);
    Optional<PublicAccusation> findByEmail(String id);
    Optional<PublicAccusation> findByMatchingIdAndEmail(int matchingId , String id);

}
