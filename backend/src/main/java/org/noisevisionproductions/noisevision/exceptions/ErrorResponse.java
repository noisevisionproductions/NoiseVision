package org.noisevisionproductions.noisevision.exceptions;

public record ErrorResponse(
        String type,
        String key
) {
}
