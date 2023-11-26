package com.example.cokkiri.utils;

import com.example.cokkiri.model.Hobby;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class HobbyUtils {
    public static final List<String> HOBBIES = Arrays.asList(
            "축구", "농구", "야구", "당구",
            "컴퓨터", "스위치", "보드게임", "오락실",
            "영화", "드라마", "뮤지컬", "전시회",
            "의류", "악세사리", "화장품", "네일",
            "강아지", "고양이", "조류", "식물",
            "그림", "음악", "사진", "글쓰기"
    );

    public List<List<Double>> hobbyScoreOfUsers(List<Optional<Hobby>> hobbyOfUsers) {

        List<List<Double>> score = new ArrayList<>();
        Optional<Hobby> other;

        for (int i = 0; i < hobbyOfUsers.size(); i++) {
            Optional<Hobby> user = hobbyOfUsers.get(i);

            List<Double> scoreOfUser = new ArrayList<>();


            for (int j = 0; j < hobbyOfUsers.size(); j++) {
                if (i == j) {
                    scoreOfUser.add(-1.0);
                    continue;
                }
                other = hobbyOfUsers.get(j);

                List<String> unions = user.get().getHobby();
                List<String> retain = user.get().getHobby();
                List<String> otherHobby = other.get().getHobby();

                unions.addAll(otherHobby);
                retain.retainAll(otherHobby);

                scoreOfUser.add((double) (retain.size() / unions.size()));
            }
            score.add(scoreOfUser);
        }

        return score;
    }
}
