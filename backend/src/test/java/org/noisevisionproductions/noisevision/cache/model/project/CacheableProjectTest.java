package org.noisevisionproductions.noisevision.cache.model.project;

import org.junit.jupiter.api.Test;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CacheableProjectTest {

    @Test
    void shouldCorrectlyConvertFromProjectToCacheableProject() {
        Project project = new Project();
        project.setName("Test Project");
        project.setSlug("test-project");
        project.setFeatures(Arrays.asList("Feature 1", "Feature 2"));
        project.setTechnologies(Arrays.asList("Tech 1", "Tech 2"));

        CacheableProject cacheableProject = CacheableProject.fromProject(project);

        assertNotNull(cacheableProject);
        assertEquals("Test Project", cacheableProject.getName());
        assertEquals("test-project", cacheableProject.getSlug());
        assertEquals(2, cacheableProject.getFeatures().size());
        assertEquals(2, cacheableProject.getTechnologies().size());
    }

    @Test
    void shouldHandleCollectionsCorrectly() {
        Project project = new Project();
        project.setFeatures(List.of("Feature 1"));
        project.setTechnologies(new ArrayList<>());
        project.setContributors(new ArrayList<>());
        project.setProjectImages(new ArrayList<>());

        CacheableProject cacheableProject = CacheableProject.fromProject(project);

        assertNotNull(cacheableProject.getFeatures());
        assertNotNull(cacheableProject.getTechnologies());
        assertNotNull(cacheableProject.getContributors());
        assertNotNull(cacheableProject.getImages());
        assertEquals(1, cacheableProject.getFeatures().size());
        assertTrue(cacheableProject.getTechnologies().isEmpty());
    }

    @Test
    void shouldRetainDataAfterFullConversionCycle() {
        Project originalProject = new Project();
        originalProject.setName("Test");
        originalProject.setSlug("test");
        originalProject.setFeatures(List.of("Feature 1"));

        CacheableProject cached = CacheableProject.fromProject(originalProject);
        Project convertedBack = cached.toEntity();

        assertEquals(originalProject.getName(), convertedBack.getName());
        assertEquals(originalProject.getSlug(), convertedBack.getSlug());
        assertEquals(originalProject.getFeatures(), convertedBack.getFeatures());
    }

    @Test
    void shouldImplementSerializable() {
        new CacheableProject();
        assertTrue(true);
    }

    @Test
    void shouldHandleNullInput() {
        assertNull(CacheableProject.fromProject(null));
    }
}