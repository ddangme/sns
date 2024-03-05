package com.ddangme.sns.service;

import com.ddangme.sns.exception.ErrorCode;
import com.ddangme.sns.exception.SnsApplicationException;
import com.ddangme.sns.model.entity.PostEntity;
import com.ddangme.sns.model.entity.UserEntity;
import com.ddangme.sns.repository.PostEntityRepository;
import com.ddangme.sns.repository.UserEntityRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    PostEntityRepository postEntityRepository;

    @MockBean
    UserEntityRepository userEntityRepository;

    @DisplayName("포스트 작성 - 정상 동작")
    @Test
    void create_post() {
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName))
                .thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        // When & Then
        assertThatCode(() -> postService.create(title, body, userName))
                .doesNotThrowAnyException();
    }

    @DisplayName("포스트 작성 - 요청한 유저가 존재하지 않는 경우")
    @Test
    void create_post_no_user() {
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName))
                .thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        // When & Then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

    }
}
