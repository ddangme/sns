package com.ddangme.sns.service;

import com.ddangme.sns.exception.ErrorCode;
import com.ddangme.sns.exception.SnsApplicationException;
import com.ddangme.sns.model.*;
import com.ddangme.sns.model.entity.*;
import com.ddangme.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;

    @Transactional
    public void create(String title, String body, User loginUser) {
        // save post
        postEntityRepository.save(PostEntity.of(title, body, loginUser.getId()));
    }

    @Transactional
    public Post modify(Integer postId, String title, String body, User loginUser) {
        PostEntity postEntity = getPostEntity(postId);

        if (postEntity.noSameUser(loginUser.getId())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", loginUser.getId(), postId));
        }

        postEntity.modify(title, body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(User loginUser, Integer postId) {
        PostEntity postEntity = getPostEntity(postId);

        if (postEntity.noSameUser(loginUser.getId())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", loginUser.getId(), postId));
        }

        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);
    }

    public Page<Post> feedList(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> myFeedList(User user, Pageable pageable) {
        return postEntityRepository.findAllByUserId(user.getId(), pageable).map(Post::fromEntity);
    }

    private UserEntity getUserEntity(String userName) {
        return userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }

    private PostEntity getPostEntity(Integer postId) {
        return postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%d not founded", postId)));
    }

    @Transactional
    public void like(Integer postId, User user) {
        PostEntity postEntity = getPostEntity(postId);

        likeEntityRepository.findByUserIdAndPostId(user.getId(), postId)
                .ifPresent(it -> {
                    throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userId %s already like post %d", user.getId(), postId));
                });

        likeEntityRepository.save(LikeEntity.of(postEntity, user.getId()));
        alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(user.getId(), postId)));
    }

    @Transactional
    public long likeCount(Integer postId) {
        PostEntity postEntity = getPostEntity(postId);

        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, User user, String comment) {
        PostEntity postEntity = getPostEntity(postId);

        commentEntityRepository.save(CommentEntity.of(postEntity, user.getId(), comment));
        alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(user.getId(), postId)));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostEntity(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }
}
