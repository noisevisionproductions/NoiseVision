package org.noisevisionproductions.noisevision.auth.dto;

import java.util.Set;

public record UserInfoResponse(
        String email,
        String role,
        Set<String> authorities,
        String name,
        String companyName,
        Set<String> programmingLanguages
) {
}
