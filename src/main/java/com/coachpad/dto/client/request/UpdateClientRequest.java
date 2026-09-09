package com.coachpad.dto.client.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateClientRequest {

    @Size(min = 2, max = 20, message = "Name min size = 2, max = 20")
    private String name;

    @Size(max = 50, message = "Instagram max size = 50")
    private String instagram;

    @Size(max = 50, message = "Telegram max size = 50")
    private String telegram;

    @Size(max = 20, message = "Phone number max size = 20")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{10,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Size(max = 500, message = "Comment max size = 500")
    private String comment;

    private LocalDateTime birthDate;

    @Pattern(regexp = "^(MALE|FEMALE)$", message = "Gender must be MALE or FEMALE")
    private String gender;
}
