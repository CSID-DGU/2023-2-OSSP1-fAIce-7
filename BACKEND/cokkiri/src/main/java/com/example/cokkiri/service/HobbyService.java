package com.example.cokkiri.service;

import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.model.User;
import com.example.cokkiri.repository.HobbyRepository;
import com.example.cokkiri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Service
public class HobbyService {

    @Autowired
    private HobbyRepository hobbyRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<Hobby> getUserHobbies(String userId) {
        // userId를 기반으로 사용자의 취미 목록을 조회
        return hobbyRepository.findById(userId);
    }

    public Hobby saveHobby(String userId, Hobby hobby) {

        if (!userRepository.existsById(userId)) {
            // 사용자가 존재하지 않으면, 처리하지 않고 null 또는 예외 반환
            return null; // 또는 적절한 예외 처리
        }

        // 취미 목록을 확인하고 필요한 경우 null로 설정
        for (int i = 1; i <= 10; i++) {
            try {
                Method getMethod = Hobby.class.getMethod("getHobby" + i);
                String value = (String) getMethod.invoke(hobby);
                if (value == null) {
                    for (int j = i; j <= 10; j++) {
                        Method setMethod = Hobby.class.getMethod("setHobby" + j, String.class);
                        setMethod.invoke(hobby, new Object[]{null});
                    }
                    break;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        // 취미 저장
        return hobbyRepository.save(hobby);
    }
}
