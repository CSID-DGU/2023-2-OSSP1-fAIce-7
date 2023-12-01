package com.example.cokkiri.utils;

import com.example.cokkiri.model.Hobby;
import java.util.*;


public class HobbyUtils {
    public static HashMap<String, String> HOBBIES = new HashMap<>();

    static{
        HOBBIES.put("축구", "100");
    }
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

                // 기본 자카드 유사도 계산
                Set<String> unions = new HashSet<>(user.getHobby()); 
                Set<String> intersection = new HashSet<>(user.getHobby()); 
                intersection.retainAll(other.getHobby()); // 교집합
                unions.addAll(other.getHobby()); // 합집합

                //TODO 카테고리 유사도 계산 구현
                
                // 최종 점수
                double score = intersection.isEmpty() ? 0 : (double) intersection.size() / unions.size();
                
                // 유저 id와 score를 Pair 객체를 새로 생성해서 특정 유저가 또 다른 유저와의 선호도를 저장해서 추가
                scoresForUser.add(new Pair(otherEmail, score));
            }

            // 선호도 점수에 따라 오름차순으로 정렬
            Collections.sort(scoresForUser, Comparator.comparing(Pair::getId));
            preferenceScores.put(userEmail, scoresForUser);
        }

        return preferenceScores;

    }
}

