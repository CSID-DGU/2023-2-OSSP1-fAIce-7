package com.example.cokkiri.repository;

import com.example.cokkiri.model.NoShowClassMatchList;
import com.example.cokkiri.model.NoShowPublicMatchList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoShowClassMatchListRepository extends JpaRepository<NoShowClassMatchList,Integer> {
    public  NoShowClassMatchList findByEmail(String id);

    public NoShowClassMatchList findByMatchingIdAndMatchingType(int id, String matchingType);

}
