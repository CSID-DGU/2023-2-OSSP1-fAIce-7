package com.example.cokkiri.repository

import com.example.cokkiri.model.Hobby

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    List<Hobby> findByUser_Id(Long userId);
}
