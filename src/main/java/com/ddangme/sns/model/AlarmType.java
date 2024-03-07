package com.ddangme.sns.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmType {
    NEW_COMMENT_ON_POST("new comment"),
    NEW_LIKE_ON_POST("new like"),

    ;


    // alarmText는 DB에 저장하는 것보다, 타입으로 저장하는 것이 좋다.
    // 이유: 나중에 변경되면 모든 DB를 수정해야하기 때문이다.
    private final String alarmText;
}
