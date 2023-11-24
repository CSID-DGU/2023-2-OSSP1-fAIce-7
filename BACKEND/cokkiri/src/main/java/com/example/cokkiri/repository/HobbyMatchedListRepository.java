package com.example.cokkiri.repository;

import com.example.cokkiri.model.HobbyMatchedList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HobbyMatchedListRepository extends JpaRepository<HobbyMatchedList, Integer> {
    public List<HobbyMatchedList> findByEmailListContains(String id);
    public HobbyMatchedList findByMatchingIdAndEmailListContains(int matchingId , String id);

    public  HobbyMatchedList findByMatchingId(int id);
}
