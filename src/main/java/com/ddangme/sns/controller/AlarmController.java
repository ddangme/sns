package com.ddangme.sns.controller;

import com.ddangme.sns.controller.response.AlarmResponse;
import com.ddangme.sns.controller.response.Response;
import com.ddangme.sns.service.AlarmService;
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
        Page<AlarmResponse> alarms = alarmService.alarmList(authentication.getName(), pageable).map(AlarmResponse::fromAlarm);

        return Response.success(alarms);
    }

}
