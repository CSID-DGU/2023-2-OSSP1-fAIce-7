package com.example.cokkiri.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
public class CategoryController {

    @GetMapping("/api/interest-categories")
    public Map<String, String[]> getCategories() {
        Map<String, String[]> categories = new HashMap<>();
        categories.put("스포츠", new String[]{"축구", "농구", "야구", "당구"});
        categories.put("게임", new String[]{"컴퓨터", "스위치", "보드게임", "오락실"});
        categories.put("관람/감상", new String[]{"영화", "드라마", "뮤지컬", "전시회"});
        categories.put("미용/패션", new String[]{"의류", "악세사리", "화장품", "네일"});
        categories.put("애완동물", new String[]{"강아지", "고양이", "조류", "식물"});
        categories.put("창작", new String[]{"그림", "음악", "사진", "글쓰기"});

        return categories;
    }
}
