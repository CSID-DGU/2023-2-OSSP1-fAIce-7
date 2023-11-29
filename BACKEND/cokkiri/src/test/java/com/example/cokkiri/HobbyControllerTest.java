package com.example.cokkiri;

import com.example.cokkiri.service.HobbyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
@AutoConfigureMockMvc
public class HobbyControllerTest {



    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HobbyService hobbyService;
    private final Logger logger = LoggerFactory.getLogger(HobbyControllerTest.class);

    @Test
    public void testSaveHobby() throws Exception {
        // POST 요청 본문 데이터 생성
        String requestBody = "{\"userId\":\"yourUserId\",\"interests\":{\"interest1\":\"description1\",\"interest2\":\"description2\"}}";

        // POST 요청 보내고 결과 검증
        mockMvc.perform(MockMvcRequestBuilders.post("/api/interests/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // 로그로 Hobby 객체의 현황 출력
        logger.info("Hobby 객체의 현황: " + hobbyService.getUserHobbies("yourUserId").toString());
    }
}
