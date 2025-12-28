package org.noisevisionproductions.noisevision.cache.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    @InjectMocks
    private RedisConfig redisConfig;

    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 6379;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(redisConfig, "redisHost", TEST_HOST);
        ReflectionTestUtils.setField(redisConfig, "redisPort", TEST_PORT);
    }

    @Test
    void redisConnectionFactory_shouldCreateRedisConnectionFactory() {
        LettuceConnectionFactory connectionFactory = redisConfig.redisConnectionFactory();
        RedisStandaloneConfiguration configuration = (RedisStandaloneConfiguration) ReflectionTestUtils.getField(connectionFactory, "configuration");

        assertNotNull(connectionFactory);
        assertNotNull(connectionFactory);
        if (configuration != null) {
            assertEquals(TEST_HOST, configuration.getHostName());
            assertEquals(TEST_PORT, connectionFactory.getPort());
        }
    }

    @Test
    void redisTemplate_ShouldCreateRedisTemplate() {
        RedisTemplate<String, Object> template = redisConfig.redisTemplate();

        assertNotNull(template);
        assertInstanceOf(StringRedisSerializer.class, template.getKeySerializer());
        assertInstanceOf(GenericJackson2JsonRedisSerializer.class, template.getValueSerializer());
        assertInstanceOf(StringRedisSerializer.class, template.getHashKeySerializer());
        assertInstanceOf(GenericJackson2JsonRedisSerializer.class, template.getHashValueSerializer());
    }
}