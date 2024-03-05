package com.ddangme.sns.fixture;

import com.ddangme.sns.model.entity.PostEntity;
import com.ddangme.sns.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(Integer postId, String userName, Integer userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserName(userName);

        PostEntity result = new PostEntity();
        result.setId(postId);
        result.setUser(userEntity);

        return result;
    }
}
