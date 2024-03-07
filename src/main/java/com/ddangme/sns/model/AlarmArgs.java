package com.ddangme.sns.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmArgs {

    // 알람을 발생시킨 사람의 ID
    private Integer fromUserId;

    // 알람 주체의 ID
    private Integer targetId;
}
