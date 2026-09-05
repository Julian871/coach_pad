package com.coachpad.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotBlank(message = "Email is required")
    @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Incorrect email format"
    )
    String email;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 20, message = "Name min size = 2, max = 20")
    String name;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least 8 characters, one uppercase, one lowercase, one digit and one special character"
    )
    String password;

    @NotBlank(message = "Role is required")
    @Pattern(
            regexp = "^(TRAINER|CLIENT)$",
            message = "Role must be one of: TRAINER, CLIENT"
    )
    String role;
}
