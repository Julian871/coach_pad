package com.coachpad.security.custom;

import com.coachpad.model.enums.UserRole;

public record UserPrincipal(
        Long id,
        String name,
        Long telegramId,
        UserRole role
) {
}