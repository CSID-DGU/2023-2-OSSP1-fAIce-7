package com.example.cokkiri.repository;

import com.example.cokkiri.model.PublicMatchingWait;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublicMatchingWaitRepository extends JpaRepository<PublicMatchingWait, Integer> {
    public Optional<PublicMatchingWait> findByEmail(String id);
    public PublicMatchingWait findById(int id);

}
