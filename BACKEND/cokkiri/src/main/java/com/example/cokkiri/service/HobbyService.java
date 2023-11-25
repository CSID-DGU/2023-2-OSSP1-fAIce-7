package com.example.cokkiri.service;

import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.repository.HobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HobbyService {

    @Autowired
    private HobbyRepository hobbyRepository;

    public List<Hobby> getUserHobbies(Long userId) {
        // userId를 기반으로 사용자의 취미 목록을 조회
        return hobbyRepository.findByUser_Id(userId);
    }

<<<<<<< HEAD
    public Hobby saveHobby(Hobby hobby) {
        // 취미 저장
        return hobbyRepository.save(hobby);
=======
    public void saveUserHobbies(UserHobbiesDTO userHobbiesDTO) {
        // 유저 ID로 User 객체를 찾거나 새로 생성
        User user = userRepository.findById(userHobbiesDTO.getId())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(userHobbiesDTO.getId());
                    return userRepository.save(newUser); // 새로 생성된 User 저장
                });

        // 취미 정보 생성 및 저장
        Hobby hobby = new Hobby();
        hobby.setId(user.getId()); // User ID 설정

        Map<String, Integer> hobbyScores = userHobbiesDTO.getInterests().stream()
                .collect(Collectors.toMap(HobbyDTO::getItem, HobbyDTO::getScore));
        logger.info(userHobbiesDTO.toString());

        for (int i = 0; i < HobbyUtils.HOBBIES.size(); i++) {
            String hobbyName = HobbyUtils.HOBBIES.get(i);
            int score = hobbyScores.getOrDefault(hobbyName, 0);
            hobby.setHobby(i + 1, score);
            logger.info("Hobby: {}, Score: {}", hobbyName, score);
        }

        hobbyRepository.save(hobby);
>>>>>>> parent of dfb01f6b (버그 수정)
    }
}
