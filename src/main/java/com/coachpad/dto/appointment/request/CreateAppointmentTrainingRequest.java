package com.coachpad.dto.appointment.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentTrainingRequest {

    @NotNull(message = "Date and time are required")
    LocalDateTime dateTime;

    @NotNull(message = "Client ID is required")
    @Positive(message = "Incorrect client id")
    Long clientId;

    @Size(max = 300, message = "Max = 300")
    String comment;
}
