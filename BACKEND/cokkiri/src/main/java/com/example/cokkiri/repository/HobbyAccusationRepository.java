package com.example.cokkiri.repository;

import com.example.cokkiri.model.HobbyAccusation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HobbyAccusationRepository extends JpaRepository<HobbyAccusation,String> {
    public List<HobbyAccusation> findByMatchingType(String matchingType);
    public List<HobbyAccusation> findByMatchingIdAndMatchingType(int id, String matchingType);
    Optional<HobbyAccusation> findByEmail(String id);
    Optional<HobbyAccusation> findByMatchingIdAndEmail(int matchingId , String id);

}
