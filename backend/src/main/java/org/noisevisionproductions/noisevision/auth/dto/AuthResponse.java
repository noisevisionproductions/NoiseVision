package org.noisevisionproductions.noisevision.auth.dto;

import java.util.Set;

public record AuthResponse(
        String token,
        String email,
        String role,
        Set<String> authorities
) {
}
