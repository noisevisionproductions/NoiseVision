package org.noisevisionproductions.noisevision.cache.service.project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.noisevisionproductions.noisevision.cache.model.project.CacheableProject;
import org.noisevisionproductions.noisevision.cache.model.project.CacheableProjectsList;
import org.noisevisionproductions.noisevision.cache.service.base.CacheService;
import org.noisevisionproductions.noisevision.cache.utils.CacheKeyGenerator;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectCacheService implements CacheService<Long, Project> {
    private static final String CACHE_PREFIX = "portfolio:project";
    private static final long DEFAULT_TTL = 60;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheKeyGenerator keyGenerator;

    public List<Project> getCachedProjectsList() {
        try {
            String key = keyGenerator.generateKey(CACHE_PREFIX, "all");
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof CacheableProjectsList) {
                List<Project> projects = ((CacheableProjectsList) cached).toEntity();
                log.debug("Retrieved {} projects from cache", projects.size());
                return projects;
            }
            log.debug("No projects found in cache or invalid cache type");
        } catch (Exception e) {
            log.error("Failed to get cached projects list", e);
        }
        return null;
    }

    public void cacheProjectsList(List<Project> projects) {
        try {
            projects.forEach(project -> {
                Hibernate.initialize(project.getProjectImages());
                Hibernate.initialize(project.getContributors());
                Hibernate.initialize(project.getFeatures());
                Hibernate.initialize(project.getTechnologies());
            });

            String key = keyGenerator.generateKey(CACHE_PREFIX, "all");
            CacheableProjectsList cacheableList = CacheableProjectsList.fromProjects(projects);
            redisTemplate.opsForValue().set(key, cacheableList);
            log.debug("Successfully cached {} projects", projects.size());
        } catch (Exception e) {
            log.error("Failed to cache projects list: {}", e.getMessage(), e);
        }
    }

    @Override
    public void cache(Long id, Project project) {
        cacheWithTTL(id, project, DEFAULT_TTL, DEFAULT_TIME_UNIT);
    }

    @Override
    public void cacheWithTTL(Long id, Project project, long ttl, TimeUnit timeUnit) {
        if (id == null || project == null) {
            log.debug("Skipping cache for null id or project");
            return;
        }

        String key = keyGenerator.generateKey(CACHE_PREFIX, id.toString());
        String slugKey = keyGenerator.generateKey(CACHE_PREFIX, "slug", project.getSlug());

        try {
            CacheableProject cacheableProject = CacheableProject.fromProject(project);
            redisTemplate.opsForValue().set(key, cacheableProject, ttl, timeUnit);
            redisTemplate.opsForValue().set(slugKey, cacheableProject, ttl, timeUnit);
            log.debug("Successfully cached project with id: {}, slug: {}", id, project.getSlug());
        } catch (Exception e) {
            log.error("Failed to cache project with id: {}", id, e);
        }
    }

    @Override
    public Project get(Long id) {
        if (id == null) {
            return null;
        }

        try {
            String key = keyGenerator.generateKey(CACHE_PREFIX, id.toString());
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof CacheableProject) {
                return ((CacheableProject) cached).toEntity();
            }
        } catch (Exception e) {
            log.error("Failed to get cached project with id: {}", id, e);
        }
        return null;
    }

    @Override
    public void invalidate(Long id) {
        if (id == null) return;

        try {
            Project project = get(id);
            if (project != null) {
                String slugKey = keyGenerator.generateKey(CACHE_PREFIX, "slug", project.getSlug());
                redisTemplate.delete(slugKey);
            }

            String key = keyGenerator.generateKey(CACHE_PREFIX, id.toString());
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Failed to invalidate cache for project with id: {}", id, e);
        }
    }

    @Override
    public void invalidateProjectsList() {
        try {
            String key = keyGenerator.generateKey(CACHE_PREFIX, "all");
            redisTemplate.delete(key);
            log.debug("Successfully invalidated projects list cache");
        } catch (Exception e) {
            log.error("Failed to invalidate projects list cache", e);
        }
    }

    @Override
    public void invalidateAll(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(CACHE_PREFIX + pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("Failed to invalidate all project cache", e);
        }
    }
}
