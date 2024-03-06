package com.ddangme.sns.model;

import com.ddangme.sns.model.entity.CommentEntity;
import com.ddangme.sns.model.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Comment {
    private Integer id;
    private Integer postId;
    private String comment;
    private String userName;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Comment fromEntity(CommentEntity entity) {
        return new Comment(
                entity.getId(),
                entity.getPost().getId(),
                entity.getComment(),
                entity.getUser().getUserName(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }


}
