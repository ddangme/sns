package com.ddangme.sns.service;

import com.ddangme.sns.exception.ErrorCode;
import com.ddangme.sns.exception.SnsApplicationException;
import com.ddangme.sns.model.Post;
import com.ddangme.sns.model.entity.LikeEntity;
import com.ddangme.sns.model.entity.PostEntity;
import com.ddangme.sns.model.entity.UserEntity;
import com.ddangme.sns.repository.LikeEntityRepository;
import com.ddangme.sns.repository.PostEntityRepository;
import com.ddangme.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;

    @Transactional
    public void create(String title, String body, String userName) {
        // find user
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() ->
                        new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // save post
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(String userName, Integer postId, String title, String body) {
        UserEntity userEntity = getUserEntity(userName);

        // post exist
        PostEntity postEntity = getPostEntity(postId);

        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(String userName, Integer postId) {
        UserEntity userEntity = getUserEntity(userName);

        // post exist
        PostEntity postEntity = getPostEntity(postId);

        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable) {
        UserEntity userEntity = getUserEntity(userName);
        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
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
    public void like(Integer postId, String userName) {
        UserEntity userEntity = getUserEntity(userName);
        PostEntity postEntity = getPostEntity(postId);

        likeEntityRepository.findByUserAndPost(userEntity, postEntity)
                .ifPresent(it -> {
                    throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("username %s already like post %d", userName, postId));
                });

        likeEntityRepository.save(LikeEntity.of(postEntity, userEntity));
    }

    @Transactional
    public Integer likeCount(Integer postId) {
        PostEntity postEntity = getPostEntity(postId);

        return likeEntityRepository.countByPost(postEntity);
    }

}
