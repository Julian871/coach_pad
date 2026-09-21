package com.coachpad.controller;

import com.coachpad.dto.auth.request.TelegramAuthRequest;
import com.coachpad.dto.auth.response.AccessTokenResponse;
import com.coachpad.security.SecurityUtil;
import com.coachpad.security.custom.UserPrincipal;
import com.coachpad.service.TelegramAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TelegramAuthController {

    private final TelegramAuthService telegramAuthService;
    private final SecurityUtil securityUtil;

    @PostMapping("/telegram")
    public ResponseEntity<AccessTokenResponse> loginWithTelegram(
            @Valid @RequestBody TelegramAuthRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(telegramAuthService.loginWithTelegram(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(telegramAuthService.refresh(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        telegramAuthService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserPrincipal> me() {
        return ResponseEntity.ok(securityUtil.getCurrentUserPrincipal());
    }
}
