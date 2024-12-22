package org.noisevisionproductions.portfolio.cache.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CacheKeyGeneratorTest {

    private final CacheKeyGenerator cacheKeyGenerator = new CacheKeyGenerator();

    @Test
    void shouldGenerateKeyWithPrefixOnly() {
        String key = cacheKeyGenerator.generateKey("prefix");

        assertEquals("prefix", key);
    }

    @Test
    void shouldGenerateKeyWithPrefixAndOnePart() {
        String key = cacheKeyGenerator.generateKey("prefix", "part1");

        assertEquals("prefix:part1", key);
    }

    @Test
    void shouldGenerateKeyWithPrefixAndMultipleParts() {
        String key = cacheKeyGenerator.generateKey("prefix", "part1", "part2", "part3");

        assertEquals("prefix:part1:part2:part3", key);
    }

    @Test
    void shouldHandleEmptyParts() {
        String key = cacheKeyGenerator.generateKey("prefix", "", "", "part3");

        assertEquals("prefix:::part3", key);
    }

    @Test
    void shouldGenerateKeyWithSpecialCharacters() {
        String key = cacheKeyGenerator.generateKey("prefix", "special@#$", "part:with:colons");

        assertEquals("prefix:special@#$:part:with:colons", key);
    }

    @Test
    void shouldGenerateKeyWithNumericParts() {
        String key = cacheKeyGenerator.generateKey("prefix", "123", "456");

        assertEquals("prefix:123:456", key);
    }

    @Test
    void shouldHandleEmptyPartsArray() {
        String key = cacheKeyGenerator.generateKey("prefix");

        assertEquals("prefix", key);
    }
}