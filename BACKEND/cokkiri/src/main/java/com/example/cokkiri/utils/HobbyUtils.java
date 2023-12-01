package com.example.cokkiri.utils;

import com.example.cokkiri.model.Hobby;
import java.util.*;

public class HobbyUtils {
    public static HashMap<String, String> hobbies = new HashMap<>();
    static{
        hobbies.put("전시회 관람", "000");
        hobbies.put("미술 활동", "001");
        hobbies.put("사진 촬영", "002");
        hobbies.put("박물관 관람", "010");
        hobbies.put("음악 연주회 관람", "020");
        hobbies.put("악기 연주/노래 교실", "021");
        hobbies.put("전통예술공연 관람", "030");
        hobbies.put("전통 예술 배우기", "031");
        hobbies.put("연극 공연 관람", "040");
        hobbies.put("연극", "041");
        hobbies.put("무용 공연 관람", "050");
        hobbies.put("연예 공연 관람", "051");
        hobbies.put("춤/무용", "052");
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
        hobbies.put("수영관람", "1c1");
        hobbies.put("윈드서핑", "1c2");
        hobbies.put("윈드서핑관람", "1c3");
        hobbies.put("수상스키", "1c4");
        hobbies.put("수상스키관람", "1c5");
        hobbies.put("스킨스쿠버다이빙", "1c6");
        hobbies.put("래프팅", "1c7");
        hobbies.put("요트", "1c8");
        hobbies.put("스노보드", "1d0");
        hobbies.put("스노보드관람", "1d1");
        hobbies.put("스키", "1d2");
        hobbies.put("스키관람", "1d3");
        hobbies.put("아이스 스케이트", "1d4");
        hobbies.put("아이스 스케이트관람", "1d5");
        hobbies.put("아이스 하키", "1d6");
        hobbies.put("아이스 하키관람", "1d");
        hobbies.put("보디빌딩", "1e0");
        hobbies.put("헬스", "1e1");
        hobbies.put("에어로빅", "1f0");
        hobbies.put("요가", "1f1");
        hobbies.put("필라테스", "1f2");
        hobbies.put("배드민턴", "1g0");
        hobbies.put("배드민턴관람", "1g1");
        hobbies.put("줄넘기", "1h0");
        hobbies.put("줄넘기관람", "1h1");
        hobbies.put("체조", "1h2");
        hobbies.put("체조관람", "1h3");
        hobbies.put("훌라후프", "1h4");
        hobbies.put("훌라후프관람", "1h5");
        hobbies.put("마라톤", "1i0");
        hobbies.put("마라톤관람", "1i1");
        hobbies.put("태권도", "1j0");
        hobbies.put("태권도관람", "1j1");
        hobbies.put("유도", "1j2");
        hobbies.put("유도관람", "1j3");
        hobbies.put("합기도", "1j4");
        hobbies.put("합기도관람", "1j5");
        hobbies.put("검도", "1j6");
        hobbies.put("검도관람", "1j7");
        hobbies.put("권투", "1j8");
        hobbies.put("권투관람", "1j9");
        hobbies.put("탱고", "1k0");
        hobbies.put("왈츠", "1k1");
        hobbies.put("자이보", "1k2");
        hobbies.put("맘보", "1k3");
        hobbies.put("폴카", "1k4");
        hobbies.put("차차차", "1k5");
        hobbies.put("사이클링", "1l0");
        hobbies.put("사이클링관람", "1l1");
        hobbies.put("산악자전거", "1m0");
        hobbies.put("산악자전거관람", "1m1");
        hobbies.put("인라인 스케이트", "1n0");
        hobbies.put("인라인 스케이트관람", "1n1");
        hobbies.put("승마", "1o0");
        hobbies.put("승마관람", "1o1");
        hobbies.put("클라이밍", "1p0");
        hobbies.put("클라이밍관람", "1p1");
        hobbies.put("문화유적방문", "200");
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
        hobbies.put("수집활동", "300");
        hobbies.put("생활공예", "310");
        hobbies.put("요리", "320");
        hobbies.put("다도", "321");
        hobbies.put("반려동물 돌보기", "330");
        hobbies.put("노래방 가기", "340");
        hobbies.put("인테리어", "350");
        hobbies.put("등산", "360");
        hobbies.put("낚시", "370");
        hobbies.put("홈페이지/블로그 관리", "380");
        hobbies.put("SNS", "381");
        hobbies.put("미디어 제작", "382");
        hobbies.put("인터넷 서핑", "383");
        hobbies.put("컴퓨터게임", "390");
        hobbies.put("모바일게임", "391");
        hobbies.put("콘솔게임", "392");
        hobbies.put("보드게임", "3a0");
        hobbies.put("퍼즐/큐브", "3a1");
        hobbies.put("바둑", "3a0");
        hobbies.put("체스", "3a1");
        hobbies.put("장기", "3a2");
        hobbies.put("쇼핑", "3b0");
        hobbies.put("외식", "3c0");
        hobbies.put("독서", "3d0");
        hobbies.put("만화", "3e0");
        hobbies.put("피부관리", "3f0");
        hobbies.put("헤어관리", "3f1");
        hobbies.put("네일아트", "3f2");
        hobbies.put("마사지", "3f3");
        hobbies.put("공부", "3g0");
        hobbies.put("이색/테마카페 체험", "3h0");
        hobbies.put("원예", "3i0");
        hobbies.put("산책", "400");
        hobbies.put("목욕/사우나/찜질방", "410");
        hobbies.put("낮잠", "420");
        hobbies.put("TV시청", "430");
        hobbies.put("영상시청", "431");
        hobbies.put("라디오/팟캐스트 청취", "440");
        hobbies.put("음악 감상", "450");
        hobbies.put("신문/잡지 보기", "460");
        hobbies.put("사회봉사활동", "500");
        hobbies.put("종교활동", "510");
        hobbies.put("클럽/나이트/디스코/캬바레 가기", "520");
        hobbies.put("기타", "530");
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

