package com.example.cokkiri.service;

import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.model.User;
import com.example.cokkiri.repository.HobbyRepository;
import com.example.cokkiri.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;



@Service
public class HobbyService {
    private static final Logger logger = LoggerFactory.getLogger(HobbyService.class);


    @Autowired
    private HobbyRepository hobbyRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<Hobby> getUserHobbies(String userId) {
        // userId를 기반으로 사용자의 취미 목록을 조회
        return hobbyRepository.findById(userId);
    }

    public Hobby saveHobby(String userId, Map<String, String> hobbies) {

        // 사용자 존재 여부 확인
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            // 사용자가 존재하지 않으면, 처리하지 않고 null 또는 예외 반환
            return null; // 또는 적절한 예외 처리
        }

        Optional<Hobby> userHobbyOptional = hobbyRepository.findById(userId);
        Hobby hobby;

        if (userHobbyOptional.isPresent()) {
            hobby = userHobbyOptional.get();
        } else {
            hobby = new Hobby();
            hobby.setId(userId);
        }

        // userId 설정
        hobby.setId(userId);

        for (int i = 1; i <= 10; i++) {
            String hobbyItem = hobbies.get("item" + i);
            if (hobbyItem != null) {
                logger.info("hobbyItem: {}", hobbyItem);
                // 해당 item 번호에 해당하는 hobby 속성을 설정
                hobby.setHobby(i, hobbyItem);
            } else {
                // 해당 item 번호에 데이터가 없으면 해당 hobby 속성을 null로 설정
                hobby.setHobby(i, null);
            }
        }

        user.get().setSetInterests(true);
        // 취미 저장
        return hobbyRepository.save(hobby);
    }


}
