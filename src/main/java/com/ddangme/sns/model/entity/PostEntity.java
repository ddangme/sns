package com.ddangme.sns.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "post")
@Getter
@SQLDelete(sql = "UPDATE post SET deleted_at = NOW() WHERE post_id = ?")
@Where(clause = "deleted_at is NULL")
@NoArgsConstructor
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private Timestamp registeredAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;

    public PostEntity(String title, String body, Integer userId) {
        this.title = title;
        this.body = body;
        this.user = UserEntity.of(userId);
    }

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static PostEntity of(String title, String body, Integer userId) {
        return new PostEntity(title, body, userId);
    }

    public boolean noSameUser(Integer userId) {
        return !user.getId().equals(userId);
    }

    public void modify(String title, String body) {
        this.title = title;
        this.body = body;
    }



}
