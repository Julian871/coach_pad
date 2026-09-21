package com.coachpad.service;

import com.coachpad.dto.auth.request.TelegramAuthRequest;
import com.coachpad.dto.auth.response.AccessTokenResponse;
import com.coachpad.exception.ApiException;
import com.coachpad.model.entity.RefreshTokenEntity;
import com.coachpad.model.entity.UserEntity;
import com.coachpad.model.enums.UserRole;
import com.coachpad.repository.UserRepository;
import com.coachpad.security.jwt.JwtUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAuthService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final CookieService cookieService;

    public AccessTokenResponse loginWithTelegram(TelegramAuthRequest request, HttpServletResponse response) {
        JsonNode userData = validateAndExtractUserData(request.getInitData());

        Long telegramId = userData.get("id").asLong();
        String firstName = userData.has("first_name") ? userData.get("first_name").asText() : "User";

        UserEntity user = userRepository.findByTelegramId(telegramId)
                .map(existingUser -> {
                    existingUser.setName(firstName);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .telegramId(telegramId)
                            .name(firstName)
                            .role(UserRole.valueOf(request.getRole()))
                            .build();
                    return userRepository.save(newUser);
                });

        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user.getTelegramId());

        refreshTokenService.saveRefreshToken(user, refreshToken, jwtUtils.getRefreshExpiration());
        cookieService.addRefreshTokenCookie(response, refreshToken);

        return new AccessTokenResponse(accessToken);
    }

    public AccessTokenResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String oldRefreshToken = cookieService.getRefreshTokenFromCookies(request);

        if (oldRefreshToken == null) {
            throw new ApiException("Refresh token not found", HttpStatus.UNAUTHORIZED);
        }

        if (!jwtUtils.validateToken(oldRefreshToken)) {
            throw new ApiException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        Long telegramId = jwtUtils.getTelegramIdFromRefreshToken(oldRefreshToken);
        UserEntity user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.UNAUTHORIZED));;

        RefreshTokenEntity oldTokenEntity = refreshTokenService.findByToken(oldRefreshToken);

        if (oldTokenEntity.isRevoked() || oldTokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ApiException("Refresh token expired or revoked", HttpStatus.UNAUTHORIZED);
        }

        refreshTokenService.revokeToken(oldRefreshToken);

        String newAccessToken = jwtUtils.generateAccessToken(user);
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getTelegramId());

        refreshTokenService.saveRefreshToken(user, newRefreshToken, jwtUtils.getRefreshExpiration());
        cookieService.addRefreshTokenCookie(response, newRefreshToken);

        return new AccessTokenResponse(newAccessToken);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.getRefreshTokenFromCookies(request);

        if (refreshToken == null) {
            throw new ApiException("Refresh token not found", HttpStatus.UNAUTHORIZED);
        }

        if (!jwtUtils.validateToken(refreshToken)) {
            throw new ApiException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        refreshTokenService.revokeToken(refreshToken);
        cookieService.clearRefreshTokenCookie(response);
    }

    private JsonNode validateAndExtractUserData(String initData) {
        try {
            Map<String, String> params = parseQueryString(initData);

            String hash = params.get("hash");
            if (hash == null) {
                throw new ApiException("Invalid Telegram data: hash missing", HttpStatus.UNAUTHORIZED);
            }

            StringBuilder dataCheckString = new StringBuilder();
            params.entrySet().stream()
                    .filter(entry -> !"hash".equals(entry.getKey()))
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        if (dataCheckString.length() > 0) {
                            dataCheckString.append("\n");
                        }
                        dataCheckString.append(entry.getKey()).append("=").append(entry.getValue());
                    });

            byte[] secretKey = hmacSha256("WebAppData".getBytes(StandardCharsets.UTF_8), botToken.getBytes(StandardCharsets.UTF_8));
            byte[] calculatedHashBytes = hmacSha256(secretKey, dataCheckString.toString().getBytes(StandardCharsets.UTF_8));
            String calculatedHash = bytesToHex(calculatedHashBytes);

            if (!calculatedHash.equalsIgnoreCase(hash)) {
                throw new ApiException("Telegram authentication hash verification failed", HttpStatus.UNAUTHORIZED);
            }

            String userJson = params.get("user");
            if (userJson == null) {
                throw new ApiException("Invalid Telegram data: user payload missing", HttpStatus.BAD_REQUEST);
            }

            return objectMapper.readTree(userJson);

        } catch (Exception e) {
            if (e instanceof ApiException) throw (ApiException) e;
            throw new ApiException("Failed to parse Telegram initData", HttpStatus.UNAUTHORIZED);
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                map.put(key, value);
            }
        }
        return map;
    }

    private byte[] hmacSha256(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}