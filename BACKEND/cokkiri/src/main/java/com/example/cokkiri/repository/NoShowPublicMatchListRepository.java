package com.example.cokkiri.repository;

import com.example.cokkiri.model.NoShowPublicMatchList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoShowPublicMatchListRepository extends JpaRepository<NoShowPublicMatchList,Integer> {
    public NoShowPublicMatchList findByEmail(String id);

    public NoShowPublicMatchList findByMatchingIdAndMatchingType(int id, String matchingType);
}
