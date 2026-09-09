package com.coachpad.security;

import com.coachpad.exception.ApiException;
import com.coachpad.model.entity.UserEntity;
import com.coachpad.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserService userService;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getCurrentUserEmail() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException("User not authenticated", HttpStatus.UNAUTHORIZED);
        }
        return authentication.getName();
    }

    public UserEntity getCurrentUser() {
        return userService.getUserByEmail(getCurrentUserEmail());
    }
}
