package com.example.cokkiri;

import com.example.cokkiri.dto.HobbyDTO;
import com.example.cokkiri.dto.UserHobbiesDTO;
import com.example.cokkiri.model.Hobby;
import com.example.cokkiri.model.User;
import com.example.cokkiri.repository.HobbyRepository;
import com.example.cokkiri.repository.UserRepository;
import com.example.cokkiri.service.HobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class HobbyServiceTest {

    @Mock
    private HobbyRepository hobbyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HobbyService hobbyService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId("temp@gmail.com");
    }

    @Test
    public void testSaveUserHobbies() {
        // 유저 정보 설정
        when(userRepository.findById("temp@gmail.com")).thenReturn(Optional.of(testUser));

        // 취미 정보 설정
        UserHobbiesDTO userHobbiesDTO = new UserHobbiesDTO();
        userHobbiesDTO.setId("temp@gmail.com");
        userHobbiesDTO.setHobbies(Arrays.asList(
                new HobbyDTO("스포츠", "축구", 8),
                new HobbyDTO("게임", "컴퓨터", 2)
        ));

        // 서비스 메소드 호출
        hobbyService.saveUserHobbies(userHobbiesDTO);

        // 검증
        verify(userRepository, times(1)).findById("temp@gmail.com");
        verify(hobbyRepository, times(1)).save(any(Hobby.class));

        // ArgumentCaptor를 사용하여 save 메소드에 전달된 Hobby 객체 캡처
        ArgumentCaptor<Hobby> hobbyCaptor = ArgumentCaptor.forClass(Hobby.class);
        verify(hobbyRepository).save(hobbyCaptor.capture());

        // 캡처된 Hobby 객체의 상태를 출력
        Hobby capturedHobby = hobbyCaptor.getValue();
        System.out.println("Hobby scores for user " + capturedHobby.getId() + ":");
        IntStream.rangeClosed(1, 24).forEach(i -> {
            int score = capturedHobby.getHobbyScore(i); // getHobbyScore 메소드는 각 취미의 점수를 반환합니다.
            System.out.println("Hobby " + i + ": " + score);
        });
    }
}
