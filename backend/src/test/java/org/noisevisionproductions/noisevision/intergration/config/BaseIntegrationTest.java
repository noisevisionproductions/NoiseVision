package org.noisevisionproductions.noisevision.intergration.config;

import org.junit.jupiter.api.BeforeEach;
import org.noisevisionproductions.noisevision.projectsManagement.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Objects;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.test.constructor.autowire.mode=all"
})
public class BaseIntegrationTest {

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        cleanupDatabase();
        cleanupRedis();
    }

    private void cleanupRedis() {
        projectRepository.deleteAll();
    }

    private void cleanupDatabase() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll();
    }
}
