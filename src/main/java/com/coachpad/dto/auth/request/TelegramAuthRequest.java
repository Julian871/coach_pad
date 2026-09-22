package com.coachpad.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TelegramAuthRequest {

    @NotBlank(message = "initData is required")
    private String initData;

    @NotBlank(message = "Role is required")
    @Pattern(
            regexp = "^(TRAINER|CLIENT)$",
            message = "Role must be TRAINER or CLIENT"
    )
    private String role;
}
