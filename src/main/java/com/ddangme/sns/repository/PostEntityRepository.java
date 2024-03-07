package com.ddangme.sns.repository;

import com.ddangme.sns.model.User;
import com.ddangme.sns.model.entity.PostEntity;
import com.ddangme.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {

    Page<PostEntity> findAllByUser(UserEntity user, Pageable pageable);

    Page<PostEntity> findAllByUserId(@Param("userId") Integer userId, Pageable pageable);
}
