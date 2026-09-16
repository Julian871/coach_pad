package com.coachpad.dto.client.response;

import java.time.LocalDateTime;

public record ClientResponse(
        Long id,
        String name,
        String instagram,
        String telegram,
        String phoneNumber,
        String comment,
        LocalDateTime birthDate,
        String gender,
        String createdAt
) {
}
