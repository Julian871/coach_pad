package com.coachpad.service;

import com.coachpad.model.entity.RefreshTokenEntity;
import com.coachpad.model.entity.UserEntity;
import com.coachpad.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenEntity saveRefreshToken(UserEntity user, String token, long expMillis) {
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plus(expMillis, ChronoUnit.MILLIS))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }
}
