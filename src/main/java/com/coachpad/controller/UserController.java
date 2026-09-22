package com.coachpad.controller;

import com.coachpad.dto.user.response.UserStatisticResponse;
import com.coachpad.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAuthority('TRAINER')")
    @GetMapping("/stats")
    public ResponseEntity<UserStatisticResponse> loginWithTelegram() {
        return ResponseEntity.ok(userService.getUserStatistic());
    }
}
