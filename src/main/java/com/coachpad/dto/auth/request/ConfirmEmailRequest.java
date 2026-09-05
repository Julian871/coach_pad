package com.coachpad.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmEmailRequest {

    @NotBlank(message = "Email cannot be blank")
    String email;

    @NotBlank(message = "Token cannot be blank")
    String token;
}