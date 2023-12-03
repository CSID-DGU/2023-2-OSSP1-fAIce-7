package com.example.cokkiri.utils;

import com.example.cokkiri.model.Hobby;
import java.util.*;

public class HobbyUtils {

    public static HashMap<String, String> hobbies = new HashMap<>();
    static {
        hobbies.put("전시회 관람 (미술, 사진, 건축, 디자인 등)", "000");
        hobbies.put("미술 활동(그림, 서예, 조각, 디자인, 도예, 만화 등)", "001");
        hobbies.put("사진 촬영(디지털 카메라 포함)", "002");
        hobbies.put("박물관 관람", "010");
        hobbies.put("음악 연주회 관람(클래식, 오페라 등)", "020");
        hobbies.put("악기 연주/노래 교실", "021");
        hobbies.put("전통예술공연 관람(국악, 민속놀이 등)", "030");
        hobbies.put("전통예술 배우기(사물놀이, 줄타기 등)", "031");
        hobbies.put("연극공연 관람(뮤지컬 포함)", "040");
        hobbies.put("연극", "041");
        hobbies.put("무용 공연 관람", "050");
        hobbies.put("연예 공연 관람(쇼, 콘서트, 마술 쇼 등)", "051");
        hobbies.put("춤/무용(발레, 한국무용, 현대무용, 방송댄스, 스트릿댄스, 비보잉 등)", "052");
        hobbies.put("문학 행사 참여", "060");
        hobbies.put("글짓기/독서 토론", "061");
        hobbies.put("영화 관람", "070");
        hobbies.put("농구", "100");
        hobbies.put("농구 관람", "101");
        hobbies.put("배구", "110");
        hobbies.put("배구 관람", "111");
        hobbies.put("야구", "120");
        hobbies.put("야구 관람", "121");
        hobbies.put("축구", "130");
        hobbies.put("축구 관람", "131");
        hobbies.put("족구", "140");
        hobbies.put("족구 관람", "141");
        hobbies.put("테니스", "150");
        hobbies.put("테니스 관람", "151");
        hobbies.put("스쿼시", "160");
        hobbies.put("스쿼시 관람", "161");
        hobbies.put("당구", "170");
        hobbies.put("당구 관람", "171");
        hobbies.put("포켓볼", "180");
        hobbies.put("포켓볼 관람", "181");
        hobbies.put("볼링", "190");
        hobbies.put("볼링 관람", "191");
        hobbies.put("탁구", "1a0");
        hobbies.put("탁구 관람", "1a1");
        hobbies.put("골프", "1b0");
        hobbies.put("골프 관람", "1b1");
        hobbies.put("수영", "1c0");
        hobbies.put("수영 관람", "1c1");
        hobbies.put("윈드서핑", "1c2");
        hobbies.put("윈드서핑 관람", "1c3");
        hobbies.put("수상스키", "1c4");
        hobbies.put("수상스키 관람", "1c5");
        hobbies.put("스킨스쿠버다이빙", "1c6");
        hobbies.put("래프팅", "1c7");
        hobbies.put("요트", "1c8");
        hobbies.put("스노보드", "1d0");
        hobbies.put("스노보드관람", "1d1");
        hobbies.put("스키", "1d2");
        hobbies.put("스키 관람", "1d3");
        hobbies.put("아이스 스케이트", "1d4");
        hobbies.put("아이스 스케이트 관람", "1d5");
        hobbies.put("아이스 하키", "1d6");
        hobbies.put("아이스 하키 관람", "1d7");
        hobbies.put("보디빌딩", "1e0");
        hobbies.put("헬스", "1e1");
        hobbies.put("에어로빅", "1f0");
        hobbies.put("요가", "1f1");
        hobbies.put("필라테스", "1f2");
        hobbies.put("배드민턴", "1g0");
        hobbies.put("배드민턴 관람", "1g1");
        hobbies.put("줄넘기", "1h0");
        hobbies.put("줄넘기 관람", "1h1");
        hobbies.put("체조", "1h2");
        hobbies.put("체조 관람", "1h3");
        hobbies.put("훌라후프", "1h4");
        hobbies.put("훌라후프 관람", "1h5");
        hobbies.put("마라톤", "1i0");
        hobbies.put("마라톤 관람", "1i1");
        hobbies.put("태권도", "1j0");
        hobbies.put("태권도 관람", "1j1");
        hobbies.put("유도", "1j2");
        hobbies.put("유도 관람", "1j3");
        hobbies.put("합기도", "1j4");
        hobbies.put("합기도 관람", "1j5");
        hobbies.put("검도", "1j6");
        hobbies.put("검도 관람", "1j7");
        hobbies.put("권투", "1j8");
        hobbies.put("권투 관람", "1j9");
        hobbies.put("탱고", "1k0");
        hobbies.put("왈츠", "1k1");
        hobbies.put("자이보", "1k2");
        hobbies.put("맘보", "1k3");
        hobbies.put("폴카", "1k4");
        hobbies.put("차차차", "1k5");
        hobbies.put("사이클링", "1l0");
        hobbies.put("사이클링 관람", "1l1");
        hobbies.put("산악자전거", "1m0");
        hobbies.put("산악자전거 관람", "1m1");
        hobbies.put("인라인 스케이트", "1n0");
        hobbies.put("인라인 스케이트 관람", "1n1");
        hobbies.put("승마", "1o0");
        hobbies.put("승마 관람", "1o1");
        hobbies.put("클라이밍", "1p0");
        hobbies.put("클라이밍 관람", "1p1");
        hobbies.put("문화유적 방문(고궁, 절, 유적지 등)", "200");
        hobbies.put("자연명승 및 풍경 관람", "210");
        hobbies.put("삼림욕", "211");
        hobbies.put("국내여행", "220");
        hobbies.put("해외여행", "221");
        hobbies.put("소풍/야유회", "222");
        hobbies.put("유람선 타기", "223");
        hobbies.put("온천/해수욕", "230");
        hobbies.put("테마파크/놀이공원/동물원/식물원 가기", "231");
        hobbies.put("지역축제 참가", "232");
        hobbies.put("자동차 드라이브", "240");
        hobbies.put("수집 활동(스크랩 포함)", "300");
        hobbies.put("생활공예(십자수, 비즈공예, DIY, 꽃꽂이 등)", "310");
        hobbies.put("요리", "320");
        hobbies.put("다도", "321");
        hobbies.put("반려 동물 돌보기", "330");
        hobbies.put("노래방 가기", "340");
        hobbies.put("인테리어(집, 자동차 등)", "350");
        hobbies.put("등산", "360");
        hobbies.put("낚시", "370");
        hobbies.put("홈페이지/블로그 관리", "380");
        hobbies.put("SNS", "381");
        hobbies.put("미디어 제작", "382");
        hobbies.put("인터넷 서핑", "383");
        hobbies.put("컴퓨터 게임", "390");
        hobbies.put("모바일 게임", "391");
        hobbies.put("콘솔 게임", "392");
        hobbies.put("보드 게임", "3a0");
        hobbies.put("퍼즐/큐브", "3a1");
        hobbies.put("바둑", "3b0");
        hobbies.put("체스", "3b1");
        hobbies.put("장기", "3b2");
        hobbies.put("쇼핑", "3c0");
        hobbies.put("외식", "3d0");
        hobbies.put("독서(웹소설 포함)", "3e0");
        hobbies.put("만화(애니, 웹툰)", "3f0");
        hobbies.put("피부 관리", "3g0");
        hobbies.put("헤어 관리", "3g1");
        hobbies.put("네일 아트", "3g2");
        hobbies.put("마사지", "3g3");
        hobbies.put("공부", "3h0");
        hobbies.put("이색/테마카페 체험(방탈출, VR, 낚시카페 등)", "3i0");
        hobbies.put("원예(화분, 화단 가꾸기 등)", "3j0");
        hobbies.put("산책", "400");
        hobbies.put("목욕/사우나/찜질방", "410");
        hobbies.put("낮잠", "420");
        hobbies.put("TV시청", "430");
        hobbies.put("영상시청(VOD, 유튜브, 넷플릭스, 웨이브, 티빙, 디즈니플러스 등)", "431");
        hobbies.put("라디오/팟캐스트 청취", "440");
        hobbies.put("음악 감상", "450");
        hobbies.put("신문/잡지 보기", "460");
        hobbies.put("사회봉사활동", "500");
        hobbies.put("종교 활동", "510");
        hobbies.put("클럽/나이트/디스코/캬바레 가기", "520");
        hobbies.put("기타", "530");
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
                String userHobbyBit = hobbies.getOrDefault(userHobby, "530");  //해시맵에서 해당하는 값을 찾아서 할당(해당 값이 없을 경우 "000")
                String otherHobby = extractHobby(otherFullHobby);  //>> 이후 내용만 추출
                String otherHobbyBit = hobbies.getOrDefault(otherHobby, "530");
                totalScore += compareHobbyBits(userHobbyBit, otherHobbyBit);  //두 취미의 비트 값을 비교하여 점수 계산
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
                unions.addAll(other.getHobby());

                Set<String> intersection = new HashSet<>(user.getHobby());
                intersection.retainAll(other.getHobby());

                double score = intersection.isEmpty() ? 0 : (double) (intersection.size() / unions.size())*0.6;  //카테고리 점수와 맞추기 위해 값 정규화
                score += calculateCategoryScore(user, other);  //카테고리 점수 계산
                scoresForUser.add(new Pair(otherEmail, score));
            }

            // 선호도 점수에 따라 오름차순으로 정렬
            Collections.sort(scoresForUser, Comparator.comparing(Pair::getId));
            preferenceScores.put(userEmail, scoresForUser);
        }

        return preferenceScores;
    }
}