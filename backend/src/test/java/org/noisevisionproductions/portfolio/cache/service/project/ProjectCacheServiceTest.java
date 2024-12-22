package org.noisevisionproductions.portfolio.cache.service.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.cache.model.project.CacheableProject;
import org.noisevisionproductions.portfolio.cache.model.project.CacheableProjectsList;
import org.noisevisionproductions.portfolio.cache.utils.CacheKeyGenerator;
import org.noisevisionproductions.portfolio.projectsManagement.model.Project;
import org.noisevisionproductions.portfolio.projectsManagement.model.ProjectStatus;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private CacheKeyGenerator keyGenerator;

    @InjectMocks
    private ProjectCacheService projectCacheService;

    private Project createMockProject() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setSlug("test-project");
        project.setStatus(ProjectStatus.IN_PROGRESS);
        return project;
    }

    @Test
    void shouldSuccessfullyCacheProject() {
        Project mockProject = createMockProject();
        String testKey = "portfolio:project:1";
        String testSlugKey = "portfolio:project:slug:test-project";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(keyGenerator.generateKey("portfolio:project", "1")).thenReturn(testKey);
        when(keyGenerator.generateKey("portfolio:project", "slug", "test-project")).thenReturn(testSlugKey);

        projectCacheService.cache(1L, mockProject);

        verify(valueOperations).set(eq(testKey), any(CacheableProject.class), eq(60L), eq(TimeUnit.MINUTES));
        verify(valueOperations).set(eq(testSlugKey), any(CacheableProject.class), eq(60L), eq(TimeUnit.MINUTES));

    }

    @Test
    void shouldReturnNullWhenGettingNonExistentProject() {
        Project result = projectCacheService.get(null);

        assertNull(result);
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    void shouldSuccessfullyGetCachedProject() {
        Project mockProject = createMockProject();
        CacheableProject mockCacheableProject = CacheableProject.fromProject(mockProject);
        String testKey = "portfolio:project:1";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(keyGenerator.generateKey("portfolio:project", "1")).thenReturn(testKey);
        when(valueOperations.get(testKey)).thenReturn(mockCacheableProject);

        Project result = projectCacheService.get(1L);

        assertNotNull(result);
        assertEquals(mockProject.getName(), result.getName());
        assertEquals(mockProject.getSlug(), result.getSlug());
    }

    @Test
    void shouldSuccessfullyCacheProjectsList() {
        Project mockProject = createMockProject();
        List<Project> projects = List.of(mockProject);
        String allProjectsKey = "portfolio:project:all";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(keyGenerator.generateKey("portfolio:project", "all")).thenReturn(allProjectsKey);

        projectCacheService.cacheProjectsList(projects);

        verify(valueOperations).set(eq(allProjectsKey), any(CacheableProjectsList.class));

    }

    @Test
    void shouldSuccessfullyInvalidateAllCache() {
        String pattern = "*";
        Set<String> keys = new HashSet<>(Arrays.asList("key1", "key2"));
        when(redisTemplate.keys("portfolio:project" + pattern)).thenReturn(keys);

        projectCacheService.invalidateAll(pattern);

        verify(redisTemplate).delete(keys);
    }

    @Test
    void shouldSuccessfullyGetCachedProjectsList() {
        Project mockProject = createMockProject();
        List<Project> projects = List.of(mockProject);
        CacheableProjectsList cacheableProjectsList = CacheableProjectsList.fromProjects(projects);
        String allProjectsKey = "portfolio:project:all";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(keyGenerator.generateKey("portfolio:project", "all")).thenReturn(allProjectsKey);
        when(valueOperations.get(allProjectsKey)).thenReturn(cacheableProjectsList);

        List<Project> result = projectCacheService.getCachedProjectsList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockProject.getName(), result.getFirst().getName());
        assertEquals(mockProject.getSlug(), result.getFirst().getSlug());
    }

    @Test
    void shouldSuccessfullyInvalidateProjectCache() {
        Project mockProject = createMockProject();
        CacheableProject mockCacheableProject = CacheableProject.fromProject(mockProject);
        String projectKey = "portfolio:project:1";
        String slugKey = "portfolio:project:slug:test-project";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(keyGenerator.generateKey("portfolio:project", "1")).thenReturn(projectKey);
        when(valueOperations.get(projectKey)).thenReturn(mockCacheableProject);
        when(keyGenerator.generateKey("portfolio:project", "slug", "test-project")).thenReturn(slugKey);

        projectCacheService.invalidate(1L);

        verify(redisTemplate).delete(projectKey);
        verify(redisTemplate).delete(slugKey);
    }

    @Test
    void shouldNotInvalidateWhenProjectIdIsNull() {
        projectCacheService.invalidate(null);

        verify(redisTemplate, never()).delete(anyString());
        verify(redisTemplate, never()).opsForValue();
    }
}