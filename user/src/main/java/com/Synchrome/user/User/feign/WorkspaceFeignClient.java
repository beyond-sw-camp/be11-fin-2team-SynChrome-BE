package com.Synchrome.user.User.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "workspace-service", url = "https://server.synchrome.shop")
public interface WorkspaceFeignClient {
    @PostMapping("/workspace-service/calendar/create/user")
    void createCalendarForUser(
            @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("Authorization") String token
    );
}
