package com.Synchrome.user.User.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "workspace-service", url = "http://localhost:8080")
public interface WorkspaceFeignClient {
    @PostMapping("/workspace-service/calendar/create/user")
    Long createCalendarForUser(
            @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("Authorization") String token
    );

    @GetMapping("/workspace-service/calendar/user/{userId}")
    Long getOrCreateCalendarId(
            @PathVariable("userId") Long userId,
            @RequestHeader("Authorization") String token);
}
