package com.example.cokkiri.repository;

import com.example.cokkiri.model.ClassMatchedList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassMatchedListRepository extends JpaRepository<ClassMatchedList,Integer> {
    public List<ClassMatchedList> findByEmailListContains(String id);

    public  ClassMatchedList findByMatchingIdAndEmailListContains(int matchingId, String id);

    public ClassMatchedList findByMatchingId(int id);

}
