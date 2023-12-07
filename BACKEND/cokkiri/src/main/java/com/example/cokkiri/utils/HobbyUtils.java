package com.example.cokkiri.utils;

import com.example.cokkiri.model.Hobby;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

@Component
public class HobbyUtils {

    private static final Logger logger = Logger.getLogger(HobbyUtils.class.getName());
    static HashMap<String, String> hobbies;

    public static HashMap<String, String> loadHobbiesFromCSV() throws IOException, CsvException {
        HashMap<String, String> hobbies = new HashMap<>();
        Resource resource = new ClassPathResource("hobbies.csv");
        InputStream inputStream = resource.getInputStream();
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String[]> allRows = reader.readAll();
            for (String[] row : allRows) {
                if (row.length >= 2) {
                    hobbies.put(row[0], row[1]);
                }
            }
        }
        return hobbies;
    }

    @PostConstruct
    public void init() throws IOException, CsvException {
        hobbies = loadHobbiesFromCSV();
    }

    // ">>"를 기준으로 문자열을 분할해 두 번째 요소를 반환
    public static String extractHobby(String fullHobby) {
        String[] parts = fullHobby.split(">>");  // ">>"를 기준으로 문자열을 분할
        // 분할된 문자열 배열의 두 번째 요소(인덱스 1)가 취미 이름
        // 배열 길이를 확인하여 ">>" 이후의 부분이 있는지 확인해 반환
        return parts.length > 1 ? parts[1].trim() : "";
    }

    //두 사용자의 취미를 비교하여 점수를 계산하는 메소드(카테고리 점수)
    public static double calculateCategoryScore(Hobby user, Hobby other) {
        List<String> userHobbies = user.getHobby();  //사용자의 취미 목록
        List<String> otherHobbies = other.getHobby();  //다른 사용자의 취미 목록

        int totalScore = 0;  //총점
        int compareCount = 0;  //비교 횟수
        for (String userFullHobby : userHobbies) {  //사용자와 다른 사용자의 취미들을 모두 비트 연산
            if(userFullHobby == null) break;  //null인 취미가 나오면 이후 취미는 비교 X
            for (String otherFullHobby : otherHobbies) {
                if(otherFullHobby == null) break;  //null인 취미가 나오면 이후 취미는 비교 X

                String userHobby = extractHobby(userFullHobby);  //>> 이후 내용만 추출
                String userHobbyBit = hobbies.getOrDefault(userHobby, "530");  //해시맵에서 해당하는 값을 찾아서 할당(해당 값이 없을 경우 "530" 기타로 분류)
                String otherHobby = extractHobby(otherFullHobby);  //>> 이후 내용만 추출
                String otherHobbyBit = hobbies.getOrDefault(otherHobby, "530");
                totalScore += compareHobbyBits(userHobbyBit, otherHobbyBit);  //두 취미의 비트 값을 비교하여 점수 계산  //해시맵에서 해당하는 값을 찾아서 할당(해당 값이 없을 경우 "530" 기타로 분류)
                compareCount++;
            }
        }
        return compareCount > 0 ? (double)(totalScore/compareCount)*(1/3.0) : 0;  //평균 점수 반환, 자카드 점수와 맞추기 위해 값 정규화
    }

    // 두 취미의 비트 값을 비교하여 점수 계산
    private static int compareHobbyBits(String userHobbyBit, String otherHobbyBit) {
        int score = 0;
        for (int i = 0; i < userHobbyBit.length(); i++) {  //userHobbyBit 각 문자에 대해 반복
            if (userHobbyBit.charAt(i) == otherHobbyBit.charAt(i)) {  //userHobbyBit와 otherHobbyBit의 i번째 문자가 같은지 비교
                score++;
            }
            else  //두 비트가 다를 경우 현재까지의 점수를 반환하고 뒤의 비트는 계산하지 않음
                return score;
        }
        return score;
    }
    public static Map<String, List<Pair>> hobbyScoreOfUsers(List<Optional<Hobby>> hobbyOfUsers) {
        Map<String, List<Pair>> preferenceScores = new HashMap<>();
        logger.info("취미 해시맵 출력: " + hobbies);

        for (Optional<Hobby> userOpt : hobbyOfUsers) {
            if (!userOpt.isPresent()) continue;
            Hobby user = userOpt.get();
            String userEmail = user.getId();
            List<Pair> scoresForUser = new ArrayList<>();

            for (Optional<Hobby> otherOpt : hobbyOfUsers) {
                if (!otherOpt.isPresent() || otherOpt == userOpt) continue;
                Hobby other = otherOpt.get();
                String otherEmail = other.getId();

                if (userEmail.equals(otherEmail)) {
                    continue;
                }

                Set<String> unions = new HashSet<>(user.getHobby());
                unions.addAll(other.getHobby());

                Set<String> intersection = new HashSet<>(user.getHobby());
                intersection.retainAll(other.getHobby());

                int unionSize;  //자카드 유사도의 분모를 합집합의 크기에 따라 조정
                if(unions.size() <= 2)  //2이하 2
                    unionSize = 2;
                else if(unions.size() >= 6)  //6이상 6
                    unionSize = 6;
                else  //2에서 6사이면 2~6
                    unionSize=unions.size();

                double score = intersection.isEmpty() ? 0 : (double) (intersection.size() / unionSize)*0.6;  //카테고리 점수와 맞추기 위해 값 정규화
                score += calculateCategoryScore(user, other);  //카테고리 점수 계산
                scoresForUser.add(new Pair(otherEmail, score));
            }

            // 선호도 점수에 따라 오름차순으로 정렬
            Collections.sort(scoresForUser, Comparator.comparing(Pair::getId));
            preferenceScores.put(userEmail, scoresForUser);

            // 선호도 점수를 로깅합니다.
            logger.info("User ID: " + userEmail);
            for (Pair pair : scoresForUser) {
                String otherUserId = pair.getId();
                double score = pair.getScore();
                logger.info("  Other User ID: " + otherUserId + ", Score: " + score);
            }
        }
        return preferenceScores;
    }
}