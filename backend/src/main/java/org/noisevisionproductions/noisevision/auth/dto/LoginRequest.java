package org.noisevisionproductions.noisevision.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.ToString;

public record LoginRequest(
        @NotBlank(message = "emailRequired")
        String email,

        @NotBlank(message = "passwordRequired")
        @Size(min = 6, message = "passwordLength")
        @ToString.Exclude
        String password
) {
}
