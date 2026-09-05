package com.coachpad.service;

import com.coachpad.dto.auth.request.ConfirmEmailRequest;
import com.coachpad.dto.auth.request.LoginRequest;
import com.coachpad.dto.auth.request.RegistrationRequest;
import com.coachpad.dto.auth.response.AccessTokenResponse;
import com.coachpad.exception.auth.UserNotFoundException;
import com.coachpad.model.entity.RefreshTokenEntity;
import com.coachpad.security.custom.CustomUserDetails;
import com.coachpad.security.jwt.JwtUtils;
import com.coachpad.service.handler.auth.RegistrationHandler;
import com.coachpad.model.entity.UserEntity;
import com.coachpad.repository.UserRepository;
import com.coachpad.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RegistrationHandler registrationHandler;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final CookieService cookieService;

    public void register(RegistrationRequest request) {
        registrationHandler.register(request);
    }

    @Transactional
    public void confirmEmail(ConfirmEmailRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);
        if(user.isConfirmed())
            throw new ApiException("Email is confirmed", HttpStatus.BAD_REQUEST);

        if(!user.getConfirmationToken().toString().equals(request.getToken()))
            throw new ApiException("Incorrect token", HttpStatus.UNAUTHORIZED);

        user.setConfirmed(true);
        user.setConfirmationToken(null);
        userRepository.save(user);
    }

    public AccessTokenResponse login(LoginRequest request, HttpServletResponse response) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.UNAUTHORIZED));

        if (!user.isConfirmed()) {
            throw new ApiException("Account not confirmed. Please verify your email.", HttpStatus.UNAUTHORIZED);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtUtils.generateAccessToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication.getName());

            refreshTokenService.saveRefreshToken(user, refreshToken, jwtUtils.getRefreshExpiration());

            cookieService.addRefreshTokenCookie(response, refreshToken);

            return new AccessTokenResponse(accessToken);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for {}: {}", request.getEmail(), e.getMessage());
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }

    public AccessTokenResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String oldRefreshToken = cookieService.getRefreshTokenFromCookies(request);

        if (oldRefreshToken == null)
            throw new ApiException("Refresh token not found", HttpStatus.UNAUTHORIZED);

        if (!jwtUtils.validateToken(oldRefreshToken)) {
            throw new ApiException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        String email = jwtUtils.getEmailFromToken(oldRefreshToken);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.UNAUTHORIZED));

        RefreshTokenEntity oldTokenEntity = refreshTokenService.findByToken(oldRefreshToken);

        if (oldTokenEntity.isRevoked() || oldTokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ApiException("Refresh token expired or revoked", HttpStatus.UNAUTHORIZED);
        }

        refreshTokenService.revokeToken(oldRefreshToken);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = jwtUtils.generateAccessToken(authentication);
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        refreshTokenService.saveRefreshToken(user, newRefreshToken, jwtUtils.getRefreshExpiration());

        cookieService.addRefreshTokenCookie(response, newRefreshToken);

        return new AccessTokenResponse(newAccessToken);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.getRefreshTokenFromCookies(request);

        if (refreshToken == null)
            throw new ApiException("Refresh token not found", HttpStatus.UNAUTHORIZED);

        if (!jwtUtils.validateToken(refreshToken)) {
            throw new ApiException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        refreshTokenService.revokeToken(refreshToken);

        cookieService.clearRefreshTokenCookie(response);
    }
}
