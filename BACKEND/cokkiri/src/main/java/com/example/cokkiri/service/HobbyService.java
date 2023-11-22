package com.example.cokkiri.service;

import com.example.cokkiri.repository.UserRepository;
import com.example.cokkiri.model.User;
import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.repository.HobbyRepository;
import com.example.cokkiri.dto.UserHobbiesDTO;
import com.example.cokkiri.dto.HobbyDTO;
import com.example.cokkiri.utils.HobbyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HobbyService {

    @Autowired
    private HobbyRepository hobbyRepository;

    @Autowired
    private UserRepository userRepository;

    public void saveUserHobbies(UserHobbiesDTO userHobbiesDTO) {
        User user = userRepository.findById(userHobbiesDTO.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Hobby hobby = new Hobby();
        hobby.setUser(user);

        Map<String, Integer> hobbyScores = userHobbiesDTO.getHobbies().stream()
                .collect(Collectors.toMap(HobbyDTO::getCategory, HobbyDTO::getScore));

        for (int i = 0; i < HobbyUtils.HOBBIES.size(); i++) {
            String hobbyName = HobbyUtils.HOBBIES.get(i);
            int score = hobbyScores.getOrDefault(hobbyName, 0);
            hobby.setHobby(i + 1, score); // 1부터 시작하는 hobby 필드 인덱스에 맞추어 점수 설정
        }

        hobbyRepository.save(hobby);
    }
}
