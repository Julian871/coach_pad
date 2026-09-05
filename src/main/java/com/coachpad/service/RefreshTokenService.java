package com.coachpad.service;

import com.coachpad.exception.ApiException;
import com.coachpad.model.entity.RefreshTokenEntity;
import com.coachpad.model.entity.UserEntity;
import com.coachpad.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(UserEntity user, String token, long expMillis) {
        String tokenHash = hashToken(token);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiryDate(LocalDateTime.now().plus(expMillis, ChronoUnit.MILLIS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    public void revokeToken(String token) {
        String tokenHash = hashToken(token);

        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    public RefreshTokenEntity findByToken(String token) {
        String tokenHash = hashToken(token);

        return refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ApiException("Token not found", HttpStatus.UNAUTHORIZED));
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found", e);
        }
    }
}
