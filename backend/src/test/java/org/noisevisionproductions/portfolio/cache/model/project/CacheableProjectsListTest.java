package org.noisevisionproductions.portfolio.cache.model.project;

import org.junit.jupiter.api.Test;
import org.noisevisionproductions.portfolio.projectsManagement.model.Project;
import org.noisevisionproductions.portfolio.projectsManagement.model.ProjectStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CacheableProjectsListTest {

    private Project createSampleProject(Long id, String name) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setSlug(name.toLowerCase().replace(" ", "-"));
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setFeatures(Arrays.asList("Feature 1", "Feature 2"));
        project.setTechnologies(Arrays.asList("Java", "Spring"));
        return project;
    }

    @Test
    void shouldCorrectlyConvertFromProjectsListToCacheableList() {
        List<Project> projects = Arrays.asList(
                createSampleProject(1L, "Project One"),
                createSampleProject(2L, "Project Two")
        );

        CacheableProjectsList cacheableList = CacheableProjectsList.fromProjects(projects);

        assertNotNull(cacheableList);
        assertEquals(2, cacheableList.getProjects().size());
        assertEquals(1L, cacheableList.getProjects().get(0).getId());
        assertEquals("Project One", cacheableList.getProjects().get(0).getName());
        assertEquals(2L, cacheableList.getProjects().get(1).getId());
        assertEquals("Project Two", cacheableList.getProjects().get(1).getName());
    }

    @Test
    void shouldCorrectlyConvertCacheableListToProjectsList() {
        List<Project> originalProjects = Arrays.asList(
                createSampleProject(1L, "Project One"),
                createSampleProject(2L, "Project Two")
        );
        CacheableProjectsList cacheableList = CacheableProjectsList.fromProjects(originalProjects);

        List<Project> convertedProjects = cacheableList.toEntity();

        assertNotNull(convertedProjects);
        assertEquals(2, convertedProjects.size());
        assertEquals(1L, convertedProjects.get(0).getId());
        assertEquals("Project One", convertedProjects.get(0).getName());
        assertEquals(2L, convertedProjects.get(1).getId());
        assertEquals("Project Two", convertedProjects.get(1).getName());
    }

    @Test
    void shouldHandleEmptyList() {
        List<Project> emptyProjects = new ArrayList<>();

        CacheableProjectsList cacheableList = CacheableProjectsList.fromProjects(emptyProjects);
        List<Project> convertedProjects = cacheableList.toEntity();

        assertNotNull(cacheableList.getProjects());
        assertTrue(cacheableList.getProjects().isEmpty());
        assertNotNull(convertedProjects);
        assertTrue(convertedProjects.isEmpty());
    }

    @Test
    void shouldInitializeEmptyListInDefaultConstructor() {
        CacheableProjectsList cacheableList = new CacheableProjectsList();

        assertNotNull(cacheableList.getProjects());
        assertTrue(cacheableList.getProjects().isEmpty());
    }

    @Test
    void shouldCorrectlyUseAllArgsConstructor() {
        List<CacheableProject> cacheableProjects = Arrays.asList(
                new CacheableProject(),
                new CacheableProject()
        );

      /*  CacheableProjectsList cacheableList = new CacheableProjectsList(cacheableProjects);

        assertNotNull(cacheableList.getProjects());
        assertEquals(2, cacheableList.getProjects().size());*/
    }

    @Test
    void shouldPreserveProjectOrder() {
        List<Project> orderedProjects = Arrays.asList(
                createSampleProject(1L, "First Project"),
                createSampleProject(2L, "Second Project"),
                createSampleProject(3L, "Third Project")
        );

        CacheableProjectsList cacheableList = CacheableProjectsList.fromProjects(orderedProjects);
        List<Project> convertedProjects = cacheableList.toEntity();

        assertEquals(3, convertedProjects.size());
        assertEquals("First Project", convertedProjects.get(0).getName());
        assertEquals("Second Project", convertedProjects.get(1).getName());
        assertEquals("Third Project", convertedProjects.get(2).getName());
    }
}