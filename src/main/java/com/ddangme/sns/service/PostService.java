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
    public void create(String title, String body, Integer loginUserId) {
        // save post
        postEntityRepository.save(PostEntity.of(title, body, loginUserId));
    }

    @Transactional
    public Post modify(Integer postId, String title, String body, Integer loginUserId) {
        PostEntity postEntity = getPostEntity(postId);

        if (postEntity.noSameUser(loginUserId)) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", loginUserId, postId));
        }

        postEntity.modify(title, body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(Integer loginUserId, Integer postId) {
        PostEntity postEntity = getPostEntity(postId);

        if (postEntity.noSameUser(loginUserId)) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", loginUserId, postId));
        }

        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);
    }

    public Page<Post> feedList(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> myFeedList(Integer loginUserId, Pageable pageable) {
        return postEntityRepository.findAllByUserId(loginUserId, pageable).map(Post::fromEntity);
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
    public void like(Integer postId, Integer loginUserId) {
        PostEntity postEntity = getPostEntity(postId);

        likeEntityRepository.findByUserIdAndPostId(loginUserId, postId)
                .ifPresent(it -> {
                    throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userId %s already like post %d", loginUserId, postId));
                });

        likeEntityRepository.save(LikeEntity.of(postEntity, loginUserId));
        alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(loginUserId, postId)));
    }

    @Transactional
    public long likeCount(Integer postId) {
        PostEntity postEntity = getPostEntity(postId);

        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, Integer loginUserId, String comment) {
        PostEntity postEntity = getPostEntity(postId);

        commentEntityRepository.save(CommentEntity.of(postEntity, loginUserId, comment));
        alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(loginUserId, postId)));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostEntity(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }
}
