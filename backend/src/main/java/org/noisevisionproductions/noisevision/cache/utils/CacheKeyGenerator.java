package org.noisevisionproductions.noisevision.cache.utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CacheKeyGenerator {

    private static final String SEPARATOR = ":";

    public String generateKey(String prefix, String... parts) {
        return Stream.concat(
                Stream.of(prefix),
                Arrays.stream(parts)
        ).collect(Collectors.joining(SEPARATOR));
    }
}
