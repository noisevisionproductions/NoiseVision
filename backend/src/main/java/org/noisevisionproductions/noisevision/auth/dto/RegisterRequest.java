package org.noisevisionproductions.noisevision.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.ToString;

import java.util.Set;

public record RegisterRequest(
        @Email(message = "invalidEmail")
        @NotBlank(message = "emailRequired")
        String email,

        @NotBlank(message = "passwordRequired")
        @Size(min = 6, message = "passwordLength")
        @ToString.Exclude
        String password,
        String name,
        String companyName,
        Set<String> programmingLanguages
) {
}