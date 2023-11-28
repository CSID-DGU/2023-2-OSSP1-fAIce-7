package com.example.cokkiri;

import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.repository.HobbyRepository;
import com.example.cokkiri.repository.UserRepository;
import com.example.cokkiri.service.HobbyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HobbyServiceTest {

    @Autowired
    private HobbyService hobbyService;

    @MockBean
    private HobbyRepository hobbyRepository;

    @MockBean
    private UserRepository userRepository;


    @Test
    public void whenSaveHobby_withNonExistingUser_thenHobbyIsNotSaved() {
        String userId = "user123";
        Hobby hobby = new Hobby();

        when(userRepository.existsById(userId)).thenReturn(false);

        Hobby savedHobby = hobbyService.saveHobby(userId, hobby);

        assertNull(savedHobby);
        verify(userRepository, times(1)).existsById(userId);
        verifyNoInteractions(hobbyRepository);
    }

    @Test
    public void whenSaveHobby_withExistingUser_thenHobbyIsSaved() {
        String userId = "user123";
        Hobby hobby = new Hobby();
        hobby.setId(userId); // 적절한 테스트용 ID 설정
        hobby.setHobby1("영화관람");
        hobby.setHobby2("요리");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(hobbyRepository.save(any(Hobby.class))).thenReturn(hobby); // 저장 메소드 모킹

        System.out.println("Before saving: " + hobby);

        Hobby savedHobby = hobbyService.saveHobby(userId, hobby);

        System.out.println("After saving: " + savedHobby);

        assertNotNull(savedHobby);
        verify(hobbyRepository).save(any(Hobby.class));
    }



}
