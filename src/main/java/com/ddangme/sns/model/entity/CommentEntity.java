package com.ddangme.sns.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "comment", indexes = {
        @Index(name = "post_id_idx", columnList = "post_id")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE comment SET deleted_at = NOW() WHERE post_like_id = ?")
@Where(clause = "deleted_at is NULL")
@NoArgsConstructor
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String comment;

    private Timestamp registeredAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;

    private CommentEntity(PostEntity postEntity, UserEntity userEntity, String comment) {
        this.post = postEntity;
        this.user = userEntity;
        this.comment = comment;
    }

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static CommentEntity of(PostEntity postEntity, UserEntity userEntity, String comment) {
        return new CommentEntity(postEntity, userEntity, comment);
    }

}
