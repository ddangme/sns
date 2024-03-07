package com.ddangme.sns.model.entity;

import com.ddangme.sns.model.AlarmArgs;
import com.ddangme.sns.model.AlarmType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "alarm", indexes = {
        @Index(name = "user_id_idx", columnList = "user_id")})
@Getter
@Setter
@TypeDef(name = "json", typeClass = JsonType.class)
@SQLDelete(sql = "UPDATE alarm SET deleted_at = NOW() WHERE post_like_id = ?")
@Where(clause = "deleted_at is NULL")
@NoArgsConstructor
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private AlarmArgs args;

    private Timestamp registeredAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;

    private AlarmEntity(UserEntity user, AlarmType alarmType, AlarmArgs args) {
        this.user = user;
        this.alarmType = alarmType;
        this.args = args;
    }

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static AlarmEntity of(UserEntity user, AlarmType alarmType, AlarmArgs args) {
        return new AlarmEntity(user, alarmType, args);
    }

}
