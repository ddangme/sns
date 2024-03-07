package com.ddangme.sns.controller;

import com.ddangme.sns.controller.response.AlarmResponse;
import com.ddangme.sns.controller.response.Response;
import com.ddangme.sns.exception.ErrorCode;
import com.ddangme.sns.exception.SnsApplicationException;
import com.ddangme.sns.model.User;
import com.ddangme.sns.service.AlarmService;
import com.ddangme.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    public Response<Page<AlarmResponse>> list(Pageable pageable, Authentication authentication) {
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed"));

        Page<AlarmResponse> alarms = alarmService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm);

        return Response.success(alarms);
    }

}
