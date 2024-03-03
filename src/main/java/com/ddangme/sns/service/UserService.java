package com.ddangme.sns.service;

import com.ddangme.sns.exception.SnsApplicationException;
import com.ddangme.sns.model.User;
import com.ddangme.sns.model.entity.UserEntity;
import com.ddangme.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;

    // TODO : implement
    public User join(String userName, String password) {
        // 회원가입하려는 userName으로 회원가입된 user가 있는지 확인 필요
        Optional<UserEntity> userEntity = userEntityRepository.findByUserName(userName);

        // 회원가입 진행 ( user 등록 )
        userEntityRepository.save(new UserEntity());

        return new User();
    }


    // TODO : implement
    public String login(String userName, String password) {
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(SnsApplicationException::new);

        // 비밀번호 체크
        if (!userEntity.getPassword().equals(password)) {
            throw new SnsApplicationException();
        }

        // 토큰 생성

        return "";
    }
}
