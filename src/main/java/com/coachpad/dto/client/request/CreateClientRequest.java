package com.coachpad.dto.client.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateClientRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 20, message = "Name min size = 2, max = 20")
    String name;

    @NotBlank(message = "Gender is required")
    @Pattern(
            regexp = "^(MALE|FEMALE)$",
            message = "Gender must be MALE or FEMALE"
    )
    String gender;
}
