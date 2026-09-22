package com.coachpad.dto.appointment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentPlanRequest {

    @NotNull(message = "Date and time are required")
    LocalDateTime dateTime;

    @NotBlank()
    @Size(max = 20, message = "Max = 20")
    String plan;

    @Size(max = 300, message = "Max = 25")
    String comment;
}
