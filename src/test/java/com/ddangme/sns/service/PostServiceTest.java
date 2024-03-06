package com.ddangme.sns.service;

import com.ddangme.sns.exception.ErrorCode;
import com.ddangme.sns.exception.SnsApplicationException;
import com.ddangme.sns.fixture.PostEntityFixture;
import com.ddangme.sns.model.entity.PostEntity;
import com.ddangme.sns.model.entity.UserEntity;
import com.ddangme.sns.repository.PostEntityRepository;
import com.ddangme.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    @DisplayName("포스트 수정 - 정상 동작")
    @Test
    void modify_post() {
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity post = PostEntityFixture.get(postId, userName, 1);
        UserEntity user = post.getUser();

        // mocking
        when(userEntityRepository.findByUserName(userName))
                .thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(post);

        // When & Then
        assertThatCode(() -> postService.modify(userName, postId, title, body))
                .doesNotThrowAnyException();
    }

    @DisplayName("포스트 수정 - 포스트가 존재하지 않는 경우")
    @Test
    void modify_post_none_exist_post() {
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity post = PostEntityFixture.get(postId, userName, 1);
        UserEntity user = post.getUser();

        // mocking
        when(userEntityRepository.findByUserName(userName))
                .thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());
        when(postEntityRepository.saveAndFlush(any())).thenReturn(post);

        // When & Then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.modify(userName, postId, title, body));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    @DisplayName("포스트 수정 - 유저가 존재하지 않을 경우")
    @Test
    void modify_post_none_exist_user() {
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // mocking
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // When & Then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.modify(userName, postId, title, body));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("포스트 수정 - 작성자가 아닌 경우")
    @Test
    void modify_post_not_writer() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);

        Integer postId = 1;
        String userName = "name";
        String title = "title";
        String body = "body";
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(mockPostEntity));
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mockUserEntity));
        when(mockPostEntity.getUser()).thenReturn(mock(UserEntity.class));
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.modify(userName, postId, title, body));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.INVALID_PERMISSION);
    }

    @DisplayName("포스트 삭제 - 정상 동작")
    @Test
    void delete_post() {
        // Given
        String userName = "userName";
        Integer postId = 1;

        PostEntity post = PostEntityFixture.get(postId, userName, 1);
        UserEntity user = post.getUser();

        // mocking
        when(userEntityRepository.findByUserName(userName))
                .thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(post));

        // When & Then
        assertThatCode(() -> postService.delete(userName, postId))
                .doesNotThrowAnyException();
    }

    @DisplayName("포스트 삭제 - 존재하지 않는 경우")
    @Test
    void delete_post_none_exist_post() {
        // Given
        String userName = "userName";
        Integer postId = 1;

        PostEntity post = PostEntityFixture.get(postId, userName, 1);
        UserEntity user = post.getUser();

        // mocking
        when(userEntityRepository.findByUserName(userName))
                .thenReturn(Optional.of(user));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.delete(userName, postId));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    @DisplayName("포스트 삭제 - 권한이 없는 경우")
    @Test
    void delete_post_no_have_auth() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);

        Integer postId = 1;
        String userName = "name";
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(mockPostEntity));
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mockUserEntity));
        when(mockPostEntity.getUser()).thenReturn(mock(UserEntity.class));
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.delete(userName, postId));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.INVALID_PERMISSION);
    }

    @DisplayName("피드 목록 - 정상 동작")
    @Test
    void feed_list() {
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());

        assertThatCode(() -> postService.list(pageable)).doesNotThrowAnyException();
    }

    @DisplayName("내 피드 목록 - 정상 동작")
    @Test
    void my_feed_list() {
        Pageable pageable = mock(Pageable.class);
        UserEntity user = mock(UserEntity.class);

        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findAllByUser(user, pageable)).thenReturn(Page.empty());

        assertThatCode(() -> postService.my(any(), pageable)).doesNotThrowAnyException();
    }


    @DisplayName("좋아요 - 정상 동작")
    @Test
    void post_like() {
        // Given
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);

        // When
        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(post.getId())).thenReturn(Optional.of(post));

        // Then
        assertThatCode(() -> postService.like(post.getId(), user.getUserName())).doesNotThrowAnyException();
    }

    @DisplayName("좋아요 - 게시글이 없는 경우")
    @Test
    void post_like_none_exist_post() {
        // Given
        UserEntity user = mock(UserEntity.class);
        PostEntity post = mock(PostEntity.class);

        // When
        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findById(post.getId())).thenReturn(Optional.empty());

        // Then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.like(post.getId(), user.getUserName()));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
    }





}
