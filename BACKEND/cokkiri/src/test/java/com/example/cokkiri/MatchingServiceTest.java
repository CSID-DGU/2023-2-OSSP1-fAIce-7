package com.example.cokkiri;

import com.example.cokkiri.model.HobbyMatching;
import com.example.cokkiri.repository.HobbyRepository;
import com.example.cokkiri.service.MatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // 테스트 프로파일 사용
public class MatchingServiceTest {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private HobbyRepository hobbyRepository;

    @Test
    public void testFindHobbyMatch() {
        // 테스트 데이터 준비

    }
}