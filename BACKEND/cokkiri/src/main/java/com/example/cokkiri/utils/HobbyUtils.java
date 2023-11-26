package com.example.cokkiri.utils;

import com.example.cokkiri.model.Hobby;
import java.util.*;


public class HobbyUtils {
    public static final List<String> HOBBIES = Arrays.asList(
            "축구", "농구", "야구", "당구",
            "컴퓨터", "스위치", "보드게임", "오락실",
            "영화", "드라마", "뮤지컬", "전시회",
            "의류", "악세사리", "화장품", "네일",
            "강아지", "고양이", "조류", "식물",
            "그림", "음악", "사진", "글쓰기"
    );

    public static Map<String, List<Pair>> hobbyScoreOfUsers(List<Optional<Hobby>> hobbyOfUsers) {
        Map<String, List<Pair>> preferenceScores = new HashMap<>();

        for (Optional<Hobby> userOpt : hobbyOfUsers) {
            if (!userOpt.isPresent()) continue;
            Hobby user = userOpt.get();
            String userEmail = user.getId();
            List<Pair> scoresForUser = new ArrayList<>();

            for (Optional<Hobby> otherOpt : hobbyOfUsers) {
                if (!otherOpt.isPresent() || otherOpt == userOpt) continue;
                Hobby other = otherOpt.get();
                String otherEmail = other.getId();

                Set<String> unions = new HashSet<>(user.getHobby());
                Set<String> intersection = new HashSet<>(user.getHobby());
                intersection.retainAll(other.getHobby());
                unions.addAll(other.getHobby());

                double score = intersection.isEmpty() ? 0 : (double) intersection.size() / unions.size();
                scoresForUser.add(new Pair(otherEmail, score));
            }

            // 선호도 점수에 따라 오름차순으로 정렬
            Collections.sort(scoresForUser, Comparator.comparing(Pair::getId));
            preferenceScores.put(userEmail, scoresForUser);
        }

        return preferenceScores;

    }
}

