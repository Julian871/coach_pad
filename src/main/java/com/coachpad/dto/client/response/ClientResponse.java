package com.coachpad.dto.client.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientResponse(
        UUID id,
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
