package com.example.cokkiri.service;

import com.example.cokkiri.model.*;
import com.example.cokkiri.repository.*;
import com.example.cokkiri.utils.HobbyUtils;
import com.example.cokkiri.utils.Pair;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Transactional
@EnableScheduling
@Service("matching")
public class MatchingService {
    private static final Logger logger = Logger.getLogger(MatchingService.class.getName());

    // 싱글스레드 호출
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // 수업 레포지토리
    @Autowired
    private ClassMatchedListRepository classMatchedListRepository;

    // 공강 레포지토리
    @Autowired
    private PublicMatchedListRepository publicMatchedListRepository;

    //취미 레포지토리
    @Autowired
    private HobbyMatchedListRepository hobbyMatchedListRepository;

    @Autowired
    private HobbyRepository hobbyRepository;

    @Autowired
    private NoShowPublicMatchListRepository noShowPublicMatchListRepository;
    @Autowired
    private NoShowClassMatchListRepository noShowClassMatchRepository;
    @Autowired
    private NoShowHobbyMatchListRepository noShowHobbyMatchRepository;
    @Autowired
    private AccusationRepository accusationRepository;
    @Autowired
    private PublicAccusationRepository publicAccusationRepository;
    @Autowired
    private HobbyAccusationRepository hobbyAccusationRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PublicMatchingWaitRepository publicMatchingWaitRepository;
    @Autowired
    private ClassMatchingWaitRepository classMatchingWaitRepository;
    @Autowired
    private HobbyMatchingWaitRepository hobbyMatchingWaitRepository;
    // 배열에 저장 (공강)
    List<PublicMatching> publicLectureUsers = new ArrayList<>();
    // 배열에 저장 (수업)
    List<ClassMatching> classLectureUsers = new ArrayList<>();
    //배열에 저장 (취미)
    List<HobbyMatching> hobbyLectureUsers = new ArrayList<>();

    //반환 배열
    List<PublicMatching> publicUsersList = new ArrayList<>();
    List<ClassMatching> classUserList =new ArrayList<>();

    boolean isCalculating = false;

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.info(e.toString());
            }
        }));
    }

    // 요일, 시간, 희망인원이 같을 시 증가
    int userCount;
    //매치된 user들 지우는 용도
    List<Integer> usermatched = new ArrayList<>();

    // 겹치는 학수번호 확인
    public static Set<String> findDuplicatesCourse(List<String>List) {
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (String i: List) {
            if (!seen.add(i)) {
                duplicates.add(i);
            }
        }
        return duplicates;
    }

    // 겹치는 시간 확인 알고리즘
    public static List<LocalTime> findDuplicatTime(List<LocalTime>user , List<LocalTime>lastUser) {
        LocalTime userStartTime = user.get(0); // 비교할 user 매칭가능 시작시간
        LocalTime userEndTime = user.get(1);
        LocalTime lastUserStartTime = lastUser.get(0); // 마지막으로 배열에 들어온 user 매칭가능 시작시간
        LocalTime lastUserEndTime = lastUser.get(1);
        // 겹치는 시간 파악 후
        LocalTime startTime = null;
        LocalTime endTime = null;
        if(userStartTime.isAfter(lastUserStartTime)){
            if(lastUserEndTime.isBefore(userStartTime) || lastUserEndTime.equals(userStartTime)){
                System.out.println("두 user는 시간이 겹치지 않습니다.");
                startTime = null;
                endTime = null;
            }
            else if(userEndTime.isAfter(lastUserEndTime)){
                startTime = userStartTime;
                endTime = lastUserEndTime;
            }else if(userEndTime.isBefore(lastUserEndTime)){
                startTime = userStartTime;
                endTime = userEndTime;
            }else if(userEndTime.equals(lastUserEndTime)){
                startTime = userStartTime;
                endTime = lastUserEndTime;
            }
        }
        if(lastUserStartTime.isAfter(userStartTime)){
            if(userEndTime.isBefore(lastUserStartTime) || userEndTime.equals(lastUserStartTime)){
                System.out.println("두 user는 시간이 겹치지 않습니다.");
                startTime = null;
                endTime = null;
            }
            else if(lastUserEndTime.isAfter(userEndTime)){
                startTime = lastUserStartTime;
                endTime = userEndTime;
            }
            else if(lastUserEndTime.isBefore(userEndTime)){
                startTime = lastUserStartTime;
                endTime = lastUserEndTime;
            }
            else if(lastUserEndTime.equals(userEndTime)){
                startTime = lastUserStartTime;
                endTime = lastUserEndTime;
            }
        }
        if(userStartTime.equals(lastUserStartTime)){
            if(userEndTime.equals(lastUserEndTime)){
                startTime = userStartTime;
                endTime = userEndTime;
            }
            else if(userEndTime.isAfter(lastUserEndTime)){
                startTime = userStartTime;
                endTime = lastUserEndTime;
            }
            else if(userEndTime.isBefore(lastUserEndTime)){
                startTime = userStartTime;
                endTime = userEndTime;
            }
        }
        System.out.println("시작시간은 : " +startTime);
        System.out.println("끝 시간은 : " + endTime);
        List<LocalTime> times  = new ArrayList<>();
        times.add(startTime);
        times.add(endTime);
        return times;
    }

    // 매칭 대기 api
    public PublicMatchingWait savePublicMatchingWaitUser(List<PublicMatching>userList){
        Optional<User> user = userRepository.findById(userList.get(userList.size()-1).getEmail());
        user.get().setPublicMatching(true);
        userRepository.save(user.get());

        PublicMatchingWait waitUser = new PublicMatchingWait();
        waitUser.setMatchingType(userList.get(userList.size()-1).getMatchingType());
        waitUser.setEmail(userList.get(userList.size()-1).getEmail());
        waitUser.setStatus("매칭 대기중");
        // 매칭 신청한 시간
        waitUser.setMatchingTime(LocalDate.now());

        // 사용자가 설정한 약속시간
        List<LocalTime> timeList = new ArrayList<>();
        LocalTime startTime = userList.get(userList.size()-1).getStartTime();
        LocalTime endTime = userList.get(userList.size()-1).getEndTime();
        timeList.add(startTime);
        timeList.add(endTime);
        waitUser.setPromiseTime(timeList);

        //매칭 가능한 시간
        waitUser.setAvailableDay(userList.get(userList.size()-1).getAvailableDay());
        publicMatchingWaitRepository.save(waitUser);
        return  waitUser;
    }

    public ClassMatchingWait saveClassMatchingWaitUser(List<ClassMatching>userList){
        Optional<User> user = userRepository.findById(userList.get(userList.size()-1).getEmail());
        user.get().setClassMatching(true);
        userRepository.save(user.get());

        ClassMatchingWait waitUser = new ClassMatchingWait();
        waitUser.setMatchingType(userList.get(userList.size()-1).getMatchingType());
        waitUser.setEmail(userList.get(userList.size()-1).getEmail());
        waitUser.setStatus("매칭 대기중");
        // 신청시간
        waitUser.setMatchingTime(LocalDate.now());
        // 학수번호
        waitUser.setCourseNumber(userList.get(userList.size()-1).getCourseNumber());
        classMatchingWaitRepository.save(waitUser);
        return  waitUser;
    }

    public HobbyMatchingWait saveHobbyMatchingWaitUser(List<HobbyMatching> userList) {
        logger.info("userList1: " + userList);
        if (userList.size() > 0) {
            Optional<User> user = userRepository.findById(userList.get(0).getEmail());
            user.get().setHobbyMatching(true);
            userRepository.save(user.get());

            HobbyMatchingWait waitUser = new HobbyMatchingWait();
            waitUser.setMatchingType(userList.get(userList.size()-1).getMatchingType());
            waitUser.setEmail(userList.get(userList.size()-1).getEmail());
            waitUser.setStatus("매칭 대기중");

            hobbyMatchingWaitRepository.save(waitUser);
            return waitUser;
        }
        return null;
    }

    // 매칭 대기중 유저 모두 반환
    public List<PublicMatchingWait> findAllPublicMatchingWait(){
        return publicMatchingWaitRepository.findAll();
    }

    // 이메일로 매칭 대기중 반환
    public PublicMatchingWait findPublicMatchingWaitByEmail(String id){
        Optional<PublicMatchingWait> publicMatchWait =publicMatchingWaitRepository.findByEmail(id);
        if(publicMatchWait.isEmpty()){
            return null;
        }
        else {
            return publicMatchWait.get();
        }
    }

    // 매칭 대기중 유저 모두 반환
    public List<ClassMatchingWait> findAllClassMatchingWait(){
        return classMatchingWaitRepository.findAll();
    }

    // 이메일로 매칭 대기중 반환
    public ClassMatchingWait findClassMatchingWaitByEmail(String id){
        Optional<ClassMatchingWait> classMatchingWait = classMatchingWaitRepository.findByEmail(id);
        if (classMatchingWait.isEmpty()){
            return null;
        }else{
            return classMatchingWait.get();
        }
    }

    // 매칭 대기중 유저 모두 반환
    public List<HobbyMatchingWait> findAllHobbyMatchingWait() {
        return hobbyMatchingWaitRepository.findAll();
    }

    // 이메일로 매칭 대기중 반환
    public HobbyMatchingWait findHobbyMatchingWaitByEmail(String id) {
        Optional<HobbyMatchingWait> hobbyMatchingWait = hobbyMatchingWaitRepository.findByEmail(id);
        if (hobbyMatchingWait.isEmpty()){
            return null;
        }else{
            return hobbyMatchingWait.get();
        }
    }

    public PublicMatchedList findPublicMatch(List<PublicMatching>userList , int count) {
        // 객체 생성
        PublicMatchedList matched = new PublicMatchedList();
        if (userList.size() < 2) {
            savePublicMatchingWaitUser(userList);
            return null;
        } else {
            for (int i = 0; i <= userList.size() - 2; i++) {
                LocalTime UserStartDate = userList.get(i).getStartTime();
                LocalTime UserEndDate = userList.get(i).getEndTime();
                List<LocalTime> user = new ArrayList<>();
                user.add(UserStartDate);
                user.add(UserEndDate);
                System.out.println(user);

                LocalTime lastUserStartDate = userList.get(userList.size() - 1).getStartTime();
                LocalTime lastUserEndDate = userList.get(userList.size() - 1).getEndTime();
                List<LocalTime> lastUser = new ArrayList<>();
                lastUser.add(lastUserStartDate);
                lastUser.add(lastUserEndDate);
                System.out.println(lastUser);

                List<LocalTime> times = new ArrayList<>(findDuplicatTime(user, lastUser));

                //    마지막 요소와 시간요일,희망인원이 같은 요소있으면 배열 다 돌아
                boolean day = (userList.get(i).getAvailableDay()).equals(userList.get(userList.size() - 1).getAvailableDay());
                boolean head = (userList.get(i).getHeadCount()) == (userList.get(userList.size() - 1).getHeadCount());
                if (day && head && times != null) {

                    userCount += 1;
                    //요소를 찾았지만 희망인원이 채워 졌는지 묻는 조건문
                    if (userCount + 1 == count) {
                        PublicMatching userLast = userList.get(userList.size() - 1);

                        //  반환 배열에 넣음
                        publicUsersList.add(userLast);


                        for (int j = 0; j < userList.size() - 1; j++) {

                            LocalTime UserStartDates = userList.get(j).getStartTime();
                            LocalTime UserEndDates = userList.get(j).getEndTime();
                            List<LocalTime> users = new ArrayList<>();
                            users.add(UserStartDates);
                            users.add(UserEndDates);
                            System.out.println(users);

                            LocalTime lastUserStartDates = userList.get(userList.size() - 1).getStartTime();
                            LocalTime lastUserEndDates = userList.get(userList.size() - 1).getEndTime();
                            List<LocalTime> lastUsers = new ArrayList<>();
                            lastUsers.add(lastUserStartDates);
                            lastUsers.add(lastUserEndDates);
                            System.out.println(lastUsers);

                            List<LocalTime> timess = new ArrayList<>(findDuplicatTime(users, lastUsers));

                            boolean days = (userList.get(j).getAvailableDay()).equals(userList.get(userList.size() - 1).getAvailableDay());
                            boolean heads = (userList.get(j).getHeadCount()) == (userList.get(userList.size() - 1).getHeadCount());
                            if (days && heads && timess != null) {
                                // 겹치는 시간 확인
                                matched.setPromiseTime(timess); // publicMatchedList 객체
                                publicUsersList.add(userList.get(j));
                                usermatched.add(j); // 매치 된 사용자 index 담김 . 배열

                            }
                        }
                        //마지막 요소 제거
                        userList.remove(userLast);
                        System.out.println(userList);

                        //userList 내의 매치된 유저값 삭제
                        for (int k = 0 ; k  <usermatched.size() ; k ++ ){
                            int num = usermatched.get(k);
                            userList.remove(num);
                        }
                        usermatched.clear();
                        userCount = 0;

                        // 학번 배열 생성, set
                        List<String> emailList = new ArrayList<>();
                        for (int k = 0; k <= publicUsersList.size() - 1; k++) {
                            String studentId = publicUsersList.get(k).getEmail();
                            emailList.add(studentId);
                        }
                        // 매칭된 학생들 학번 리스트
                        matched.setEmailList(emailList);
                        // 매칭 타입
                        matched.setMatchingType(userLast.getMatchingType());
                        // 매칭 가능한 요일
                        matched.setAvailableDay(userLast.getAvailableDay());
                        // 매칭 희망인원
                        matched.setHeadCount(userLast.getHeadCount());

                        matched.setMatchingRes("매칭중");

                        //매칭 시간 현재 날짜로 세팅
                        LocalDate date = LocalDate.now();
                        // 매칭 시간
                        matched.setMatchingTime(date);

                        publicUsersList.clear();

                        // entity 반환.
                        return matched;
                    }
                    //희망인원이 다 안채워 졌으면 continue
                    else {
                        continue;
                    }
                    //배열 내 요소가 다를 시,
                } else {
                    continue;
                }
            }
        }
        //끝까지 돌았는데 못찾았을 시
        userCount = 0;
        savePublicMatchingWaitUser(userList);
        return null;
    };

    //수업매칭
    public ClassMatchedList findClassMatch(List<ClassMatching>userList , int count){
        // 객체 생성
        ClassMatchedList matched = new ClassMatchedList();
        if(userList.size()<2){
            saveClassMatchingWaitUser(userList);
            return null;
        }
        else{
            for(int i =0; i <= userList.size()-2; i++){
                List<String>firstUserCourseNum = userList.get(i).getCourseNumber();
                List<String>lastUserCourseNum = userList.get(userList.size()-1).getCourseNumber();
                List<String>courseNumList = new ArrayList<>();
                courseNumList.addAll(firstUserCourseNum);
                courseNumList.addAll(lastUserCourseNum);
                //시간표가 겹치는 유저 찾아
                List<String> courseNum = new ArrayList<>(findDuplicatesCourse(courseNumList));
                System.out.println("겹치는 수업은 : " + courseNum + " 입니다");
                // 마지막 요소와 시간요일,희망인원이 같은 요소있으면 배열 다 돌아
                boolean head = (userList.get(i).getHeadCount())==(userList.get(userList.size()-1).getHeadCount());
                if (head&&courseNum!=null) {

                    userCount+=1;
                    // 요소를 찾았지만 희망인원이 채워 졌는지 묻는 조건문
                    if(userCount+1==count){
                        ClassMatching userLast = userList.get(userList.size() - 1);
                        // 반환 배열에 넣음
                        classUserList.add(userLast);


                        for(int j = 0; j < userList.size()-1 ; j++){
                            List<String>firstUserCourseNumber = userList.get(j).getCourseNumber();
                            List<String>lastUserCourseNumber = userList.get(userList.size()-1).getCourseNumber();
                            List<String>courseNumberList = new ArrayList<>();
                            courseNumberList.addAll(firstUserCourseNumber);
                            courseNumberList.addAll(lastUserCourseNumber);
                            //시간표가 겹치는 유저 찾아
                            List<String> courseNumber = new ArrayList<>(findDuplicatesCourse(courseNumberList));
                            boolean heads = (userList.get(j).getHeadCount())==(userList.get(userList.size()-1).getHeadCount());
                            if (heads&&courseNumber!=null) {

                                // 겹치는 시간 확인
                                matched.setCourseNumber(courseNumber);
                                classUserList.add(userList.get(j));
                                usermatched.add(j);

                            }
                        }
                        //마지막 요소 제거
                        userList.remove(userLast);
                        System.out.println(userList);

                        //userList 내의 매치된 유저값 삭제
                        for (int k = 0 ; k  <usermatched.size() ; k ++ ){
                            int num = usermatched.get(k);
                            System.out.println(usermatched.get(k));
                            userList.remove(num);
                        }
                        usermatched.clear();
                        userCount =0;

                        // 학번 배열 생성, set
                        List<String> emailList = new ArrayList<>();
                        for (int k = 0; k <= classUserList.size() - 1; k++) {
                            String email = classUserList.get(k).getEmail();
                            emailList.add(email);
                        }
                        // 매칭된 학생들 학번 리스트
                        matched.setEmailList(emailList);
                        // 매칭 타입
                        matched.setMatchingType(userLast.getMatchingType());

                        // 매칭 희망인원
                        matched.setHeadCount(userLast.getHeadCount());
                        matched.setMatchingRes("매칭중");

                        //매칭 시간 현재 날짜로 세팅
                        LocalDate date = LocalDate.now();

                        // 매칭 시간
                        matched.setMatchingTime(date);

                        classUserList.clear();

                        // entity 반환.
                        return matched;
                    }
                    //희망인원이 다 안채워 졌으면 continue
                    else{
                        continue;
                    }
                    // 배열 내 요소가 다를 시,
                }else{
                    continue;
                }
            }
            //끝까지 돌았는데 못찾았을 시
            userCount=0;
            saveClassMatchingWaitUser(userList);
            return null;
        }
    }

    public void findHobbyMatch(List<HobbyMatching>userList) {
        logger.info("findHobbyMatch 함수 실행 중");
        isCalculating = true;

        if(userList.size()<2){
            isCalculating = false;
            return;
        }
        else {
            // 유사도 계산
            List<Optional<Hobby>> hobbyOfUsers = new ArrayList<>();
            List<String> userId = new ArrayList<>();
            for (int i = 0; i < userList.size(); i++) {
                Optional<Hobby> userHobby = hobbyRepository.findById(userList.get(i).getEmail());
                if (userHobby.isEmpty()) {
                    continue;
                }
                userId.add(userList.get(i).getEmail());
                hobbyOfUsers.add(userHobby);
            }

            Map<String, List<Pair>> score = HobbyUtils.hobbyScoreOfUsers(hobbyOfUsers);

            // 게일 - 섀플리 알고리즘
            // 유저별로 선호도 목록과 매칭 상태를 저장하는 맵
            Map<String, Queue<Pair>> preferences = new HashMap<>();
            Map<String, String> matches = new HashMap<>(); // 매칭 결과 저장
            Map<String, String> reverseMatches = new HashMap<>();

            for (HobbyMatching user : userList) {
                Queue<Pair> prefQueue = new LinkedList<>(score.get(user.getEmail()));
                preferences.put(user.getEmail(), prefQueue);
                matches.put(user.getEmail(), null);  // 초기에는 모든 사용자가 매치되지 않은 상태
            }

            boolean changed;
            do {
                changed = false;
                for (String proposer : preferences.keySet()) {
                    if (matches.get(proposer) != null) continue; // 이미 매치된 경우 건너뛰기

                    Queue<Pair> proposerPreferences = preferences.get(proposer);
                    if (proposerPreferences.isEmpty()) continue; // 선호도 목록이 비어있으면 건너뛰기

                    Pair topChoice = proposerPreferences.poll(); // 가장 선호하는 사용자
                    String topChoiceUser = topChoice.getId();

                    String currentMatch = reverseMatches.get(topChoiceUser);
                    if (currentMatch == null || topChoice.getScore() > score.get(topChoiceUser).stream()
                            .filter(p -> p.getId().equals(proposer))
                            .findFirst().get().getScore()) {
                        // 새로운 매칭이 더 좋은 경우
                        if (currentMatch != null) {
                            matches.put(currentMatch, null); // 이전 매치 해제
                        }
                        matches.put(proposer, topChoiceUser);
                        reverseMatches.put(topChoiceUser, proposer);
                        changed = true;
                    }
                }
            } while (changed);



            logger.info("매칭 연산 결과: " + matches);
            logger.info("매칭 연산 종료");
            HashSet<String> duplicated = new HashSet<>();

            logger.info("후처리 시작");
            logger.info("매칭 entrySet: " + matches.entrySet());
            // 후처리
            for (Map.Entry<String, String> entry : matches.entrySet()) {
                // 매칭이 성공한 경우에만 이메일 추가
                HobbyMatchedList matched = new HobbyMatchedList();
                List<String> matchedEmails = new ArrayList<>();


                if (entry.getValue() != null) {
                    matchedEmails.add(entry.getKey());  // 제안자
                    matchedEmails.add(entry.getValue());  // 수락자
                    if (duplicated.contains(entry.getKey())) continue;

                    logger.info("매칭된 쌍: " + matchedEmails);

                    // 매칭된 사람들의 이메일 리스트 설정
                    matched.setEmailList(matchedEmails);

                    // 매칭 타입 설정
                    matched.setMatchingType("hobby");

                    // 매칭 희망 인원 설정
                    matched.setHeadCount(2);

                    // 매칭 결과 상태 설정
                    matched.setMatchingRes("매칭중");

                    // 매칭 시간 설정 (현재 날짜로 설정)
                    LocalDate date = LocalDate.now();
                    matched.setMatchingTime(date);

                    logger.info("취미 매칭 대기 리스트: " + userList);
                    logger.info("이메일 리스트: " + matchedEmails);
                    logger.info("매칭 결과: " + matched);

                    duplicated.add(entry.getKey());
                    duplicated.add(entry.getValue());


                }

                logger.info("중복 체크: " + duplicated);
                logger.info("후처리 종료");
                if(matched!=null) {
                    for (int i = 0; i < matchedEmails.size(); i++) {
                        String email = matchedEmails.get(i);
                        Optional<User> userMatched = userRepository.findById(email);
                        userMatched.get().setHobbyMatching(false);
                        userRepository.save(userMatched.get());
                    }
                }

                logger.info("대기중 유저 제거 시작");
                saveHobbyUser(matched, matchedEmails);
                logger.info("대기중 유저 제거 끝");
                sendSSEtoHobbyUser(matched);
            }

            // hobbyLectureUsers에서 matches에 있는 이메일을 가진 객체를 제거
            for (String email : matches.keySet()) {
                String value = matches.get(email);
                if (value != null) {
                    hobbyLectureUsers.removeIf(hobbyMatching -> email.equals(hobbyMatching.getEmail()));
                }
            }
        }
        logger.info("매칭 함수 전체 과정 종료");
        isCalculating = false;
        return;
    }

    public PublicMatchedList publicMatch(PublicMatching user){
        // 매칭된 사람 수 = 희망인원
        int count = user.getHeadCount();
        if(publicLectureUsers.contains(user.getEmail())==true){
            return null;
        }
        PublicMatchedList publicMatchedList = new PublicMatchedList();
        String id = user.getEmail();
        Optional<User> userInfo = userRepository.findById(id);
        if(userInfo.isEmpty()){
            System.out.println("회원가입 된 사용자가 아닙니다");
            return null;
        }
        if(userInfo.get().isPublicMatching()==false){
            if(userInfo.get().getRestrctionDate()==null || userInfo.get().getRestrctionDate().isBefore(LocalDateTime.now())){
                if(userInfo.get().getHeart() < 0){
                    return null;
                }
                publicLectureUsers.add(user);
                publicMatchedList = findPublicMatch(publicLectureUsers, count);
                if(publicMatchedList!=null){
                    for (int i =0 ; i < publicMatchedList.getEmailList().size(); i++){
                        String email = publicMatchedList.getEmailList().get(i);
                        Optional<User> userMatched = userRepository.findById(email);
                        userMatched.get().setPublicMatching(false);
                        userRepository.save(userMatched.get());
                    }

                    sendSSEtoPublicUser(publicMatchedList);
                    savePublicUser(publicMatchedList);
                }
            }else{
                LocalDateTime restrictionDate = userInfo.get().getRestrctionDate();
                String string = " : 매칭이 해당일자 까지 제한됩니다.";
                StringBuffer buffer = new StringBuffer(string);
                buffer.insert(0,restrictionDate);
                String str = buffer.toString();
                publicMatchedList.setMatchingRes(str);
            }
        }else{
            publicMatchedList.setMatchingRes("중복 매칭은 불가합니다.");
        }

        return publicMatchedList;
    }

    public ClassMatchedList classMatch(ClassMatching user){
        // 매칭된 사람 수 = 희망인원
        int count = user.getHeadCount();
        if(classLectureUsers.contains(user.getEmail())==true){
            return null;
        }
        ClassMatchedList classMatchedList = new ClassMatchedList();
        String id = user.getEmail();
        Optional<User> userInfo = userRepository.findById(id);
        if(userInfo.isEmpty()){
            System.out.println("회원가입 된 사용자가 아닙니다");
            return null;
        }
        if(userInfo.get().isClassMatching()==false){
            // 유저의 제한날짜가 없거나 제한 날짜가 현재 날짜 보다 전에 있으면
            if(userInfo.get().getRestrctionDate()==null || userInfo.get().getRestrctionDate().isBefore(LocalDateTime.now())){
                if(userInfo.get().getHeart() < 0){
                    return null;
                }
                classLectureUsers.add(user);
                classMatchedList = findClassMatch(classLectureUsers,count);
                if(classMatchedList!=null){ // 매치가 되면
                    for (int i =0 ; i < classMatchedList.getEmailList().size(); i++){
                        String email = classMatchedList.getEmailList().get(i);
                        Optional<User> userMatched = userRepository.findById(email);
                        userMatched.get().setPublicMatching(false);
                    }
                    sendSSEtoClassUser(classMatchedList);
                    saveClassUser(classMatchedList);
                }
            }else{
                LocalDateTime restrictionDate = userInfo.get().getRestrctionDate();
                String string = " : 매칭이 해당일자 까지 제한됩니다.";
                StringBuffer buffer = new StringBuffer(string);
                buffer.insert(0,restrictionDate);
                String str = buffer.toString();
                classMatchedList.setMatchingRes(str);
            }
        }else{
            classMatchedList.setMatchingRes("중복 매칭은 불가합니다.");
        }

        return classMatchedList;
    }


    public String hobbyMatch(HobbyMatching user){
        // 매칭된 사람 수 = 희망인원
        logger.info("hobbyMatch 호출: " + user);
        int count = user.getHeadCount();
        if(hobbyLectureUsers.contains(user)==true){
            logger.info("hobbyLectureUsers.contains(user)==true");
            return null;
        }
        HobbyMatchedList hobbyMatchedList = new HobbyMatchedList();
        String id = user.getEmail();
        Optional<User> userInfo = userRepository.findById(id);
        if(userInfo.isEmpty()){
            System.out.println("회원가입 된 사용자가 아닙니다");
            return null;
        }
        if(userInfo.get().isHobbyMatching()==false){
            if(userInfo.get().getRestrctionDate()==null || userInfo.get().getRestrctionDate().isBefore(LocalDateTime.now())){
                hobbyLectureUsers.add(user);
                List<HobbyMatching> temp = new ArrayList<>();
                temp.add(user);
                saveHobbyMatchingWaitUser(temp);
                logger.info("=========현재 매칭 큐에 있는 사람:" + hobbyLectureUsers.size() + " " + hobbyLectureUsers);
            }else{
                LocalDateTime restrictionDate = userInfo.get().getRestrctionDate();
                String string = " : 매칭이 해당일자 까지 제한됩니다.";
                StringBuffer buffer = new StringBuffer(string);
                buffer.insert(0,restrictionDate);
                String str = buffer.toString();
                hobbyMatchedList.setMatchingRes(str);
            }
        }else{
            hobbyMatchedList.setMatchingRes("중복 매칭은 불가합니다.");
        }

        return "ok";
    }


    @Scheduled(fixedDelay = 10000)
    public void doingHobbyMatching() {
        logger.info("doingHobbyMatching 함수 진입");
        if (!isCalculating){
            logger.info("findHobbyMatch 함수 실행 시작");
            findHobbyMatch(hobbyLectureUsers);
            logger.info("findHobbyMatch 함수 실행 끝");
        } else {
            return;
        }
    }

    //수업 매칭 전부 반환
    public List<ClassMatchedList> findAllClassMatching(){
        List<ClassMatchedList> matchedlist = new ArrayList<>();
        classMatchedListRepository.findAll().forEach(e->matchedlist.add(e));
        return matchedlist;
    }


    //수업 매칭 Id로 찾아서 반환
    public List<ClassMatchedList> findClassMatchingById(String id){
        return classMatchedListRepository.findByEmailListContains(id);
    }

    //공강 매칭 전부 반환
    public List<PublicMatchedList> findAllPublicMatching(){
        LocalDate nowDate = LocalDate.now();
        List<PublicMatchedList> matchedlist = new ArrayList<>();
        publicMatchedListRepository.findAll().forEach(e->matchedlist.add(e));

        return matchedlist;
    }

    //공강 매칭 Id로 찾아서 반환
    public List<PublicMatchedList> findPublicMatchingById(String id){
        return publicMatchedListRepository.findByEmailListContains(id);
    }

    //취미 매칭 전부 반환
    public List<HobbyMatchedList> findAllHobbyMatching() {
        LocalDate nowDate = LocalDate.now();

        List<HobbyMatchedList> matchedlist = new ArrayList<>();
        hobbyMatchedListRepository.findAll().forEach(e->matchedlist.add(e));

        return matchedlist;
    }

    //취미 매칭 Id로 찾아서 반환
    public List<HobbyMatchedList> findHobbyMatchingById(String id){
        return hobbyMatchedListRepository.findByEmailListContains(id);
    }

    public ClassMatchedList findClassMatchingByMatchId(int id){
        return classMatchedListRepository.findByMatchingId(id);
    }

    public PublicMatchedList findPublicMatchingByMatchId(int id){
        return publicMatchedListRepository.findByMatchingId(id);
    }

    public HobbyMatchedList findHobbyMatchingByMatchId(int id) {
        return hobbyMatchedListRepository.findByMatchingId(id);
    }

    // 매칭 타입별로 저장
    public ClassMatchedList saveClassUser(ClassMatchedList matchedList){
        for(int i = 0 ; i < matchedList.getEmailList().size() ; i++){
            String email = matchedList.getEmailList().get(i);
            Optional<User> user = userRepository.findById(email);
            user.get().setClassMatching(false); // 안정성 코드
            user.get().setHeart((user.get().getHeart())-0); //하트 10개 차감
            userRepository.save(user.get());
            Optional<ClassMatchingWait> waitUser = classMatchingWaitRepository.findByEmail(email);
            if(waitUser.isEmpty()){
                continue;
            }else{
                if(waitUser.get().getMatchingType().equals("class")){
                    classMatchingWaitRepository.delete(waitUser.get());
                }
            }
        }
        return classMatchedListRepository.save(matchedList); // 데베에 저장

    };
    public PublicMatchedList savePublicUser(PublicMatchedList matchedList){
        for(int i = 0 ; i <matchedList.getEmailList().size(); i++){
            String email = matchedList.getEmailList().get(i);
            Optional<User> user = userRepository.findById(email);
            user.get().setPublicMatching(false);
            user.get().setHeart((user.get().getHeart())-0); //하트 10개 차감
            userRepository.save(user.get());

            Optional<PublicMatchingWait> waitUser = publicMatchingWaitRepository.findByEmail(email);
            if(waitUser.isEmpty()){
                continue;
            }else{
                if(waitUser.get().getMatchingType().equals("free")){
                    publicMatchingWaitRepository.delete(waitUser.get());
                }
            }
        }
        return publicMatchedListRepository.save(matchedList); // 데베에 저장
    };

    public HobbyMatchedList saveHobbyUser(HobbyMatchedList matchedList, List<String> matchedEmails) {
        logger.info("삭제할 리스트: " + matchedList);
        logger.info("삭제할 이메일: " + matchedList.getEmailList());
        for(int i = 0 ; i < 2; i++){
            String email = matchedEmails.get(i);
            logger.info("saveHobbyUser 내의 이메일: " + email);
            Optional<User> user = userRepository.findById(email);
            user.get().setHobbyMatching(false);
            logger.info("유저 레포 저장 시작");
            userRepository.save(user.get());
            logger.info("유저 레포 저장 끝");

            Optional<HobbyMatchingWait> waitUser = hobbyMatchingWaitRepository.findByEmail(email);
            if(waitUser.isEmpty()){
                continue;
            }else{
                if(waitUser.get().getMatchingType().equals("hobby")){
                    hobbyMatchingWaitRepository.delete(waitUser.get());
                }
            }
        }
        logger.info("saveHobbyUser: 최종 매칭 결과 저장 시작");
        return hobbyMatchedListRepository.save(matchedList); // 데베에 저장
    }

    @Autowired
    SseService sseService;
    public void sendSSEtoClassUser(ClassMatchedList matchedList){
        List<String> receiver = matchedList.getEmailList();
        String content = "매칭이 성사되었습니다.";
        String type = matchedList.getMatchingType();
        for(int i = 0 ; i < receiver.size() ; i ++){
            String email = receiver.get(i);
            sseService.send(email,content,type);
        }
    }

    public void sendSSEtoPublicUser(PublicMatchedList matchedList){
        List<String> receiver = matchedList.getEmailList();
        String content = "매칭이 성사되었습니다.";
        String type = matchedList.getMatchingType();
        for(int i = 0 ; i < receiver.size() ; i ++){
            String email = receiver.get(i);
            sseService.send(email,content,type);
        }

    }

    public void sendSSEtoHobbyUser(HobbyMatchedList matchedList) {
        List<String> receiver = matchedList.getEmailList();
        String content = "매칭이 성사되었습니다.";
        String type = matchedList.getMatchingType();
        for(int i = 0 ; i < receiver.size() ; i ++){
            String email = receiver.get(i);
            sseService.send(email,content,type);
        }
    }


    //노쇼 취소
    public NoShowPublicMatchList deleteNoShowPublicUser(int id, String matchingType){
        NoShowPublicMatchList user =noShowPublicMatchListRepository.findByMatchingIdAndMatchingType(id, matchingType);
        noShowPublicMatchListRepository.delete(user);
        return  user;
    }

    public NoShowClassMatchList deleteNoShowClassUser(int id, String matchingType){
        NoShowClassMatchList user =noShowClassMatchRepository.findByMatchingIdAndMatchingType(id, matchingType);
        noShowClassMatchRepository.delete(user);
        return  user;
    }

    public NoShowHobbyMatchList deleteNoShowHobbyUser(int id, String matchingType) {
        NoShowHobbyMatchList user =noShowHobbyMatchRepository.findByMatchingIdAndMatchingType(id, matchingType);
        noShowHobbyMatchRepository.delete(user);
        return  user;
    }
    // 매치된 리스트에서 삭제
    public  String deletePublicUser(int id){
        PublicMatchedList list = publicMatchedListRepository.findByMatchingId(id);
        if (list != null) {
            publicMatchedListRepository.delete(list);
            return "삭제 되었습니다.";
        }else{
            return "해당 매칭 아이디에 맞는 리스트가 조회되지 않습니다.";
        }
    }

    public String deleteClassUser(int id){
        ClassMatchedList list = classMatchedListRepository.findByMatchingId(id);
        if (list != null) {
            classMatchedListRepository.delete(list);;
            return "삭제 되었습니다";
        }else{
            return "해당 매칭 아이디에 맞는 리스트가 조회되지 않습니다";
        }
    }

    public String deleteHobbyUser(int id) {
        HobbyMatchedList list = hobbyMatchedListRepository.findByMatchingId(id);
        if (list != null) {
            hobbyMatchedListRepository.delete(list);
            return "삭제 되었습니다";
        }else{
            return "해당 매칭 아이디에 맞는 리스트가 조회되지 않습니다";
        }
    }

    // 매치 대기 상태에서 삭제
    public PublicMatchingWait deletePublicMatchingWaitById(int id){
        PublicMatchingWait waitUser = publicMatchingWaitRepository.findById(id);
        String email = waitUser.getEmail();
        Optional<User> user = userRepository.findById(email);
        user.get().setPublicMatching(false);
        for (int i = 0 ; i < publicLectureUsers.size() ; i ++){
            if(email.equals(publicLectureUsers.get(i).getEmail())){
                publicLectureUsers.remove(publicLectureUsers.get(i)); // 배열에서 삭제
            }
        }
        userRepository.save(user.get());
        publicMatchingWaitRepository.delete(waitUser);
        return  waitUser;
    }

    public ClassMatchingWait deleteClassMatchingWaitById(int id){
        ClassMatchingWait waitUser = classMatchingWaitRepository.findById(id);
        String email = waitUser.getEmail();
        Optional<User> user = userRepository.findById(email);
        user.get().setClassMatching(false);
        for (int i = 0 ; i < classLectureUsers.size() ; i ++){
            if(email.equals(classLectureUsers.get(i).getEmail())){
                classLectureUsers.remove(classLectureUsers.get(i)); // 배열에서 삭제
            }
        }
        userRepository.save(user.get());
        classMatchingWaitRepository.delete(waitUser);
        return  waitUser;
    }

    public HobbyMatchingWait deleteHobbyMatchingWaitById(int id) {
        HobbyMatchingWait waitUser = hobbyMatchingWaitRepository.findById(id);
        String email = waitUser.getEmail();
        Optional<User> user = userRepository.findById(email);
        user.get().setClassMatching(false);
        for (int i = 0 ; i < hobbyLectureUsers.size() ; i ++){
            if(email.equals(hobbyLectureUsers.get(i).getEmail())){
                hobbyLectureUsers.remove(hobbyLectureUsers.get(i)); // 배열에서 삭제
            }
        }
        userRepository.save(user.get());
        hobbyMatchingWaitRepository.delete(waitUser);
        return  waitUser;
    }



    public String publicMatchAgree(int matchingId , String id) {
        List<String> publicAgreeEmail  = new ArrayList<>();
        PublicMatchedList matchedList = publicMatchedListRepository.findByMatchingIdAndEmailListContains(matchingId , id);
        if(matchedList == null){
            return "매칭 번호로 매칭을 조회할 수 없습니다. 매칭타입과 매칭 번호를 확인해 주세요.";
        }
        if(matchedList.getAgreeList().contains(id)){
            return "이미 매칭 완료 버튼을 누르셨습니다.";
        }else{
            publicAgreeEmail.add(id);
            matchedList.setAgreeList(publicAgreeEmail);
            matchedList.setMatchingAgree(matchedList.getMatchingAgree() + 1); // 1씩 증가
            publicMatchedListRepository.save(matchedList);
        }
        if(matchedList== null){
            return "해당 매치를 찾지 못했습니다.";
        }
        if(matchedList.getMatchingAgree() != matchedList.getHeadCount()) {
            String comment = " 매칭완료 버튼을 눌렀습니다";
            return comment;
        }else{
            matchedList.setMatchingRes("매칭완료");
            publicMatchedListRepository.save(matchedList);
            String comment = " 매칭인원이 충족되었습니다";
            return comment;
        }
    }

    public String classMatchAgree(int matchingId,String id) {
        List<String> classAgreeEmail  = new ArrayList<>();
        ClassMatchedList matchedList = classMatchedListRepository.findByMatchingIdAndEmailListContains(matchingId,id);
        if(matchedList == null){
            return "매칭 번호로 매칭을 조회할 수 없습니다. 매칭타입과 매칭 번호를 확인해 주세요.";
        }
        if(matchedList.getAgreeList().contains(id)){
            return "이미 매칭 완료 버튼을 누르셨습니다." ;
        }else{
            classAgreeEmail.add(id);
            matchedList.setAgreeList(classAgreeEmail);
            matchedList.setMatchingAgree(matchedList.getMatchingAgree() + 1); // 1씩 증가
            classMatchedListRepository.save(matchedList);
        }
        if(matchedList== null){
            return "해당 매치를 찾지 못했습니다.";
        }
        if(matchedList.getMatchingAgree() != matchedList.getHeadCount()) {
            return " 매칭완료 버튼을 눌렀습니다.";
        }else{
            matchedList.setMatchingRes("매칭완료");
            classMatchedListRepository.save(matchedList);
            return "매칭인원이 충족 되었습니다.";
        }
    }

    public String hobbyMatchAgree(int matchingId,String id) {
        List<String> HobbyAgreeEmail  = new ArrayList<>();
        HobbyMatchedList matchedList = hobbyMatchedListRepository.findByMatchingIdAndEmailListContains(matchingId,id);
        if(matchedList == null){
            return "매칭 번호로 매칭을 조회할 수 없습니다. 매칭타입과 매칭 번호를 확인해 주세요.";
        }
        if(matchedList.getAgreeList().contains(id)){
            return "이미 매칭 완료 버튼을 누르셨습니다." ;
        }else{
            HobbyAgreeEmail.add(id);
            matchedList.setAgreeList(HobbyAgreeEmail);
            matchedList.setMatchingAgree(matchedList.getMatchingAgree() + 1); // 1씩 증가
            hobbyMatchedListRepository.save(matchedList);
        }
        if(matchedList== null){
            return "해당 매치를 찾지 못했습니다.";
        }
        if(matchedList.getMatchingAgree() != matchedList.getHeadCount()) {
            return " 매칭완료 버튼을 눌렀습니다.";
        }else{
            matchedList.setMatchingRes("매칭완료");
            hobbyMatchedListRepository.save(matchedList);
            return "매칭인원이 충족 되었습니다.";
        }
    }

    // 노쇼 리스트 반환
    public List<NoShowPublicMatchList> getNoShowPublicMatchList(){
        List<NoShowPublicMatchList> noshow = new ArrayList<>();
        noShowPublicMatchListRepository.findAll().forEach(e->noshow.add(e));
        return noshow;
    }
    public List<NoShowClassMatchList> getNoShowClassMatchList(){
        List<NoShowClassMatchList> noshow = new ArrayList<>();
        noShowClassMatchRepository.findAll().forEach(e->noshow.add(e));
        return noshow;
    }

    public List<NoShowHobbyMatchList> getNoShowHobbyMatchList() {
        List<NoShowHobbyMatchList> noshow = new ArrayList<>();
        noShowHobbyMatchRepository.findAll().forEach(e->noshow.add(e));
        return noshow;
    }

    // 신고 등록, 타입별
    public ClassAccusation postClassDeclarationList(ClassAccusation accusation){
        String classType = accusation.getMatchingType();
        String email = accusation.getEmail();
        int id = accusation.getMatchingId();
        if(classType.equals("class")){
            Optional<ClassAccusation> acc = accusationRepository.findByMatchingIdAndEmail(id,email);
            if(acc.isEmpty()){
                ClassAccusation list = new ClassAccusation();
                list.setMatchingId(id);
                list.setEmail(email);
                list.setTitle(accusation.getTitle());
                list.setComment(accusation.getComment());
                list.setMatchingType(classType);
                return accusationRepository.save(list);
            }else{
                acc.get().setTitle(accusation.getTitle());
                acc.get().setComment(accusation.getComment());
                return accusationRepository.save(acc.get());
            }
        }else{
            return  null;
        }
    }

    public PublicAccusation postPublicDeclarationList(PublicAccusation accusation){
        String classType = accusation.getMatchingType();
        String email = accusation.getEmail();
        int id = accusation.getMatchingId();
        if(classType.equals("free")){
            Optional<PublicAccusation> acc = publicAccusationRepository.findByMatchingIdAndEmail(id,email);
            if(acc.isEmpty()){
                PublicAccusation list = new PublicAccusation();
                list.setMatchingId(id);
                list.setEmail(email);
                list.setTitle(accusation.getTitle());
                list.setComment(accusation.getComment());
                list.setMatchingType(classType);
                return publicAccusationRepository.save(list);
            }else{
                acc.get().setTitle(accusation.getTitle());
                acc.get().setComment(accusation.getComment());
                return publicAccusationRepository.save(acc.get());
            }
        }else{
            return  null;
        }
    }

    public HobbyAccusation postHobbyDeclarationList(HobbyAccusation accusation){
        String classType = accusation.getMatchingType();
        String email = accusation.getEmail();
        int id = accusation.getMatchingId();
        if(classType.equals("free")){
            Optional<HobbyAccusation> acc = hobbyAccusationRepository.findByMatchingIdAndEmail(id,email);
            if(acc.isEmpty()){
                HobbyAccusation list = new HobbyAccusation();
                list.setMatchingId(id);
                list.setEmail(email);
                list.setTitle(accusation.getTitle());
                list.setComment(accusation.getComment());
                list.setMatchingType(classType);
                return hobbyAccusationRepository.save(list);
            }else{
                acc.get().setTitle(accusation.getTitle());
                acc.get().setComment(accusation.getComment());
                return hobbyAccusationRepository.save(acc.get());
            }
        }else{
            return  null;
        }
    }

    public List<PublicAccusation> getPublicDeclaration(String matchingType){
        List<PublicAccusation> list = publicAccusationRepository.findByMatchingType(matchingType);
        return list;
    }

    public List<ClassAccusation> getClassDeclaration(String matchingType){
        List<ClassAccusation> list = accusationRepository.findByMatchingType(matchingType);
        return list;
    }

    public List<HobbyAccusation> getHobbyDeclaration(String matchingType) {
        List<HobbyAccusation> list = hobbyAccusationRepository.findByMatchingType(matchingType);
        return list;
    }

    public List<PublicAccusation> getPublicDeclarationList(int id, String matchingType){
        List<PublicAccusation> list =publicAccusationRepository.findByMatchingIdAndMatchingType(id, matchingType);
        return list;
    }

    public List<ClassAccusation> getClassDeclarationList(int id, String matchingType){
        List<ClassAccusation> list =accusationRepository.findByMatchingIdAndMatchingType(id, matchingType);
        return list;
    }

    public List<HobbyAccusation> getHobbyDeclarationList(int id, String matchingType){
        List<HobbyAccusation> list = hobbyAccusationRepository.findByMatchingIdAndMatchingType(id, matchingType);
        return list;
    }

    public  NoShowPublicMatchList postNoShowPublicUser(NoShowPublicMatchList user){
        LocalDateTime time = LocalDateTime.now().plusDays(7);
        Optional<User> users = userRepository.findById(user.getEmail());

        if(users.isEmpty()){
            return null;
        }else{
            if(users.get().getRestrctionDate()!=null){
                return null;
            }else{
                NoShowPublicMatchList noShowUser = new NoShowPublicMatchList();
                if(user.getMatchingId() != 0 && user.getMatchingType() !=null && user.getEmail() != null){
                    noShowUser.setEmail(user.getEmail());
                    noShowUser.setMatchingId(user.getMatchingId());
                    noShowUser.setMatchingType(user.getMatchingType());
                    noShowUser.setRestrictionTime(time);
                    Optional<User> noshowuser = userRepository.findById(user.getEmail());
                    noshowuser.get().setRestrctionDate(time); // 일주일 제한
                    userRepository.save(noshowuser.get()); // 다시 저장
                    return noShowPublicMatchListRepository.save(noShowUser);
                }else{
                    return  null;
                }
            }
        }
    }

    public  NoShowClassMatchList postNoShowClassUser(NoShowClassMatchList user){
        NoShowClassMatchList noShowUser = new NoShowClassMatchList();
        Optional<User> users = userRepository.findById(user.getEmail());

        if(users.isEmpty()){
            return null;
        }else{
            if(users.get().getRestrctionDate()!=null){
                return null;
            }else{
                if(user.getMatchingId() != 0 && user.getMatchingType() !=null && user.getEmail() != null){
                    LocalDateTime time = LocalDateTime.now().plusDays(7);
                    noShowUser.setMatchingId(user.getMatchingId());
                    noShowUser.setEmail(user.getEmail());
                    noShowUser.setMatchingType(user.getMatchingType());
                    noShowUser.setRestrictionTime(time);

                    Optional<User> noshowuser = userRepository.findById(user.getEmail());
                    noshowuser.get().setRestrctionDate(time); // 일주일 제한
                    userRepository.save(noshowuser.get()); // 다시 저장
                    return noShowClassMatchRepository.save(noShowUser);
                }else{
                    return  null;
                }
            }
        }
    }

    public NoShowHobbyMatchList postNoShowHobbyUser(NoShowHobbyMatchList user) {
        NoShowHobbyMatchList noShowUser = new NoShowHobbyMatchList();
        Optional<User> users = userRepository.findById(user.getEmail());

        if(users.isEmpty()) {
            return null;
        } else {
            if(users.get().getRestrctionDate() != null) {
                return null;
            } else {
                if(user.getMatchingId() != 0 && user.getMatchingType() != null && user.getEmail() != null) {
                    LocalDateTime time = LocalDateTime.now().plusDays(7);

                    noShowUser.setMatchingId(user.getMatchingId());
                    noShowUser.setEmail(user.getEmail());
                    noShowUser.setMatchingType(user.getMatchingType());
                    noShowUser.setRestrictionTime(time);

                    Optional<User> noshowuser = userRepository.findById(user.getEmail());
                    noshowuser.get().setRestrctionDate(time); // 일주일 제한
                    userRepository.save(noshowuser.get()); // 다시 저장
                    return noShowHobbyMatchRepository.save(noShowUser);
                } else {
                    return null;
                }
            }
        }
    }

    //하트 반환
    public  User rollbackHeart(String id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            return  null;
        }else{
            user.get().setHeart(user.get().getHeart()+10);
        }
        return  userRepository.save(user.get());
    }
    public User changeClassNoShowUserStatus(String email){
        Optional<User> noshowUser= userRepository.findById(email);
        if(noshowUser.isEmpty()){
            return  null;
        }else{
            noshowUser.get().setRestrctionDate(null);
            userRepository.save(noshowUser.get());
            NoShowClassMatchList user  = noShowClassMatchRepository.findByEmail(email);
            noShowClassMatchRepository.delete(user);
            return noshowUser.get();
        }
    }


    public User changePublicNoShowUserStatus(String email){
        Optional<User> noshowUser= userRepository.findById(email);
        if(noshowUser.isEmpty()){
            return  null;
        }else{
            noshowUser.get().setRestrctionDate(null);
            userRepository.save(noshowUser.get());

            NoShowPublicMatchList user  = noShowPublicMatchListRepository.findByEmail(email);
            noShowPublicMatchListRepository.delete(user);
            return noshowUser.get();
        }
    }

    public User changeHobbyNoShowUserStatus(String email) {
        Optional<User> noshowUser= userRepository.findById(email);
        if(noshowUser.isEmpty()){
            return  null;
        }else{
            noshowUser.get().setRestrctionDate(null);
            userRepository.save(noshowUser.get());

            NoShowHobbyMatchList user  = noShowHobbyMatchRepository.findByEmail(email);
            noShowHobbyMatchRepository.delete(user);
            return noshowUser.get();
        }
    }
}