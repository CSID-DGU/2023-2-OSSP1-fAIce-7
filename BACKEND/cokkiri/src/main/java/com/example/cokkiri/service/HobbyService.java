package com.example.cokkiri.service;

import com.example.cokkiri.repository.UserRepository;
import com.example.cokkiri.model.User;
import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.repository.HobbyRepository;
import com.example.cokkiri.dto.UserHobbiesDTO;
import com.example.cokkiri.dto.HobbyDTO;
import com.example.cokkiri.utils.HobbyUtils;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;

@Service
public class HobbyService {

    private static final Logger logger = LoggerFactory.getLogger(HobbyService.class);

    @Autowired
    private HobbyRepository hobbyRepository;

    @Autowired
    private UserRepository userRepository;

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


        logger.info(userHobbiesDTO.toString());


        for (int i = 0; i < HobbyUtils.HOBBIES.size(); i++) {
            String hobbyName = HobbyUtils.HOBBIES.get(i);
            hobby.setHobby(i + 1, hobbyName);
            logger.info("Hobby: {}", hobbyName);
        }
        logger.info("저장 완료");
        hobbyRepository.save(hobby);
    }
}
