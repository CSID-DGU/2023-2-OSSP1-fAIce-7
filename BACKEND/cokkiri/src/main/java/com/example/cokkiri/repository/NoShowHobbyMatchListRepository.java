package com.example.cokkiri.repository;

import com.example.cokkiri.model.NoShowHobbyMatchList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoShowHobbyMatchListRepository extends JpaRepository<NoShowHobbyMatchList,Integer> {
    public NoShowHobbyMatchList findByEmail(String id);

    public NoShowHobbyMatchList findByMatchingIdAndMatchingType(int id, String matchingType);
}
