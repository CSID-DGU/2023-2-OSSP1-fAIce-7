package com.example.cokkiri.repository;

import com.example.cokkiri.model.HobbyMatchingWait;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HobbyMatchingWaitRepository extends JpaRepository<HobbyMatchingWait, Integer> {
    public Optional<HobbyMatchingWait> findByEmail(String id);
    public HobbyMatchingWait findById(int id);
}
