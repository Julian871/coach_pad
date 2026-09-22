package com.coachpad.security;

import com.coachpad.exception.ApiException;
import com.coachpad.security.custom.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class SecurityUtil {

    public UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException("User not authenticated", HttpStatus.UNAUTHORIZED);
        }
        return principal;
    }

    public Long getCurrentUserId() {
        return getCurrentUserPrincipal().id();
    }
}
