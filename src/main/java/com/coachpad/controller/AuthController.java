package com.coachpad.controller;

import com.coachpad.dto.auth.request.ConfirmEmailRequest;
import com.coachpad.dto.auth.request.LoginRequest;
import com.coachpad.dto.auth.request.RegistrationRequest;
import com.coachpad.dto.auth.response.AccessTokenResponse;
import com.coachpad.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<Void> registration(@Valid @RequestBody RegistrationRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmEmail(@Valid @RequestBody ConfirmEmailRequest request) {
        authService.confirmEmail(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.refresh(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(request, response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    // todo: resend confirm code
    // todo: forgot password
}
