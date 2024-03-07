package com.ddangme.sns.repository;

import com.ddangme.sns.model.entity.LikeEntity;
import com.ddangme.sns.model.entity.PostEntity;
import com.ddangme.sns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {

    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

    long countByPost(PostEntity post);

    List<LikeEntity> findAllByPost(PostEntity post);

    // JPA는 영속성 컨테이너로 데이터를 관리하기 때문에, delete를 하더라도, delete 이전에 select 쿼리를 먼저 날린 뒤에 삭제한다.

    @Transactional
    @Modifying
    @Query("UPDATE LikeEntity entity SET deleted_at = NOW() WHERE entity.post =:post")
    void deleteAllByPost(@Param("post") PostEntity postEntity);
}
