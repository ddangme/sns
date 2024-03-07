package com.ddangme.sns.controller;

import com.ddangme.sns.controller.request.PostCommentRequest;
import com.ddangme.sns.controller.request.PostCreateRequest;
import com.ddangme.sns.controller.request.PostModifyRequest;
import com.ddangme.sns.exception.ErrorCode;
import com.ddangme.sns.exception.SnsApplicationException;
import com.ddangme.sns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private PostService postService;


    @DisplayName("포스트 작성 - 정상 동작")
    @Test
    @WithMockUser // 특정 사용자를 인증된 사용자로 만들어 테스트를 실행할 때 사용한다. 테스트를 실행할 때 인증된 사용자를 정의하지 않아도 된다.
    void create_post() throws Exception {
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest("title", "body")))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("포스트 작성 - 미로그인 상태")
    @Test
    @WithAnonymousUser // 테스트를 실행할 때 인증되지 않은 사용자로 설정한다. 이를 통해 사용자가 로그인하지 않은 상태에서의 동작을 테스트할 수 있다.
    void create_post_no_login() throws Exception {mockMvc.perform(post("/api/v1/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(new PostCreateRequest("title", "body")))
            ).andDo(print())
            .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("포스트 수정 - 정상 동작")
    @Test
    @WithMockUser
    @Disabled
    void modify_post() throws Exception {
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body")))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("포스트 수정 - 로그인하지 않은 상태")
    @Test
    @WithAnonymousUser
    void modify_post_not_login() throws Exception {
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body")))
                ).andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("포스트 수정 - 작성한 회원이 아닌 상태")
    @Test
    @WithMockUser
    void modify_post_not_writer() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(any(), eq(1), eq("title"), eq("body"));
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body")))
                ).andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @DisplayName("포스트 수정 - 해당 글이 존재하지 않는 상태")
    @Test
    @WithMockUser
    void modify_post_none_exist_post() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(any(), eq(1), eq("title"), eq("body"));
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("포스트 삭제 - 정상 동작")
    @Test
    @WithMockUser
    void delete_post() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("포스트 삭제 - 미로그인 상태")
    @Test
    @WithAnonymousUser
    void delete_post_no_login() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("포스트 삭제 - 작성자가 아닌 경우")
    @Test
    @WithMockUser
    void delete_post_not_writer() throws Exception {
        // mocking
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION))
                .when(postService).delete(any(), any());
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("포스트 삭제 - 포스트가 존재하지 않는 경우")
    @Test
    @WithMockUser
    void delete_post_none_exist_post() throws Exception {
        // mocking
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND))
                .when(postService).delete(any(), any());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("피드 목록 - 정상 동작")
    @Test
    @WithMockUser
    void feed_list() throws Exception {
        // TODO: mocking
        when(postService.feedList(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("피드 목록 - 미로그인")
    @Test
    @WithAnonymousUser
    void feed_list_none_login() throws Exception {
        // TODO: mocking
        when(postService.feedList(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("내 피드 목록 - 정상 동작")
    @Test
    @WithMockUser
    void my_feed_list() throws Exception {
        // TODO: mocking
        when(postService.myFeedList(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("좋아요 - 정상 동작")
    @Test
    @WithMockUser
    void post_like() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("좋아요 - 미로그인")
    @Test
    @WithAnonymousUser
    void post_like_none_login() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("좋아요 - 게시글이 없는 경우")
    @Test
    @WithMockUser
    void post_like_none_exist_post() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).like(any(), any());
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("댓글 - 정상 동작")
    @Test
    @WithMockUser
    void comment() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCommentRequest("comment"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("댓글 - 미로그인")
    @Test
    @WithAnonymousUser
    void comment_none_login() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCommentRequest("comment"))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("댓글 - 게시글이 없는 경우")
    @Test
    @WithMockUser
    void comment_none_exist_post() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).comment(any(), any(), any());

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCommentRequest("comment"))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }





}

