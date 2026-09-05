package com.coachpad.service;

import com.coachpad.security.jwt.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {

    @Value("${cookie.secure:false}")
    private boolean secure;

    @Value("${cookie.same-site:Strict}")
    private String sameSite;

    private final JwtUtils jwtUtils;

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(secure)
                .path("/auth")
                .maxAge(jwtUtils.getRefreshExpiration() / 1000)
                .sameSite(sameSite)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(secure)
                .path("/auth")
                .maxAge(0)
                .sameSite(sameSite)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
