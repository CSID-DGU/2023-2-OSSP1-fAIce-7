package com.example.cokkiri.repository;

import com.example.cokkiri.model.Chat;
import com.example.cokkiri.model.ClassMatchedList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Integer> {
    List<Chat> findAllByAndMatchingIdAndMatchingType(int matchingId,String matchingType);
}
