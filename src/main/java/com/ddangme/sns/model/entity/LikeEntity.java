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
@Table(name = "post_like")
@Getter
@Setter
@SQLDelete(sql = "UPDATE post_like SET deleted_at = NOW() WHERE post_like_id = ?")
@Where(clause = "deleted_at is NULL")
@NoArgsConstructor
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private Timestamp registeredAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;

    private LikeEntity(PostEntity postEntity, UserEntity userEntity) {
        this.post = postEntity;
        this.user = userEntity;
    }

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static LikeEntity of(PostEntity postEntity, Integer userId) {
        return new LikeEntity(postEntity, UserEntity.of(userId));
    }

}
