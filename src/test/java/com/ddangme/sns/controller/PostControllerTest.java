package com.ddangme.sns.controller;

import com.ddangme.sns.controller.request.PostCreateRequest;
import com.ddangme.sns.controller.request.UserJoinRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @DisplayName("포스트 작성 - 정상 동작")
    @Test
    @WithMockUser // 특정 사용자를 인증된 사용자로 만들어 테스트를 실행할 때 사용한다. 테스트를 실행할 때 인증된 사용자를 정의하지 않아도 된다.
    void create_post() throws Exception {
        String title = "title";
        String body = "body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("포스트 작성 - 미로그인 상태")
    @Test
    @WithAnonymousUser // 테스트를 실행할 때 인증되지 않은 사용자로 설정한다. 이를 통해 사용자가 로그인하지 않은 상태에서의 동작을 테스트할 수 있다.
    void create_post_no_login() throws Exception {
        String title = "title";
        String body = "body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }
}
