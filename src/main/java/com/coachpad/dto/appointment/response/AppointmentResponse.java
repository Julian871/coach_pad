package com.coachpad.dto.appointment.response;

import com.coachpad.model.enums.AppointmentType;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        LocalDateTime dateTime,
        String comment,
        AppointmentType type,
        String plan
) {
}
