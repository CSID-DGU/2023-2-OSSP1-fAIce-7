package com.example.cokkiri.repository;

import com.example.cokkiri.model.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {

    List<String> hobbyName = List.of(
            "축구", "농구", "야구", "당구",
            "컴퓨터", "스위치", "보드게임", "오락실",
            "영화", "드라마", "뮤지컬", "전시회",
            "의류", "악세사리", "화장품", "네일",
            "강아지", "고양이", "조류", "식물",
            "그림", "음악", "사진", "글쓰기");

    List<Hobby> findByUser_Id(Long userId);
}
