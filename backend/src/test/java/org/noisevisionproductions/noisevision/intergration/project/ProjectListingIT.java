package org.noisevisionproductions.noisevision.intergration.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.noisevisionproductions.noisevision.cache.service.project.ProjectCacheService;
import org.noisevisionproductions.noisevision.intergration.config.BaseIntegrationTest;
import org.noisevisionproductions.noisevision.intergration.config.KafkaTestConfig;
import org.noisevisionproductions.noisevision.intergration.config.TestRedisConfiguration;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectDTO;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.model.ProjectStatus;
import org.noisevisionproductions.noisevision.projectsManagement.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestRedisConfiguration.class, KafkaTestConfig.class})
@EnableAutoConfiguration(exclude = {
        KafkaAutoConfiguration.class
})
public class ProjectListingIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @MockitoBean
    private ProjectCacheService projectCacheService;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        reset(projectCacheService);
    }

    @Test
    @DisplayName("Should return projects from database when cache is empty")
    void shouldListAllProjectsFromDatabase() throws Exception {
        when(projectCacheService.getCachedProjectsList()).thenReturn(null);

        List<Project> projects = Arrays.asList(
                createTestProject("Project 1", "project-1"),
                createTestProject("Project 2", "project-2")
        );
        List<Project> savedProjects = projectRepository.saveAll(projects);

        MvcResult result = mockMvc.perform(get("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<ProjectDTO> returnedProjects = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(returnedProjects).hasSize(2);
        assertThat(returnedProjects)
                .extracting(ProjectDTO::getName)
                .containsExactlyInAnyOrder("Project 1", "Project 2");

        verify(projectCacheService).getCachedProjectsList();
        verify(projectCacheService).cacheProjectsList(argThat(list ->
                list.size() == 2 &&
                        list.stream()
                                .map(Project::getName)
                                .collect(Collectors.toSet())
                                .containsAll(Arrays.asList("Project 1", "Project 2"))));
    }

    @Test
    @DisplayName("Should return projects from cache when available")
    void shouldListAllProjectsFromCache() throws Exception {
        List<Project> cachedProjects = Arrays.asList(
                createTestProject("Cached Project 1", "cached-1"),
                createTestProject("Cached Project 2", "cached-2")
        );
        when(projectCacheService.getCachedProjectsList()).thenReturn(cachedProjects);

        List<Project> dbProjects = Arrays.asList(
                createTestProject("DB Project 1", "db-1"),
                createTestProject("DB Project 2", "db-2")
        );
        projectRepository.saveAll(dbProjects);

        MvcResult result = mockMvc.perform(get("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<ProjectDTO> returnedProjects = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(returnedProjects).hasSize(2);
        assertThat(returnedProjects)
                .extracting(ProjectDTO::getName)
                .containsExactlyInAnyOrder("Cached Project 1", "Cached Project 2")
                .doesNotContain("DB Project 1", "DB Project 2");

        verify(projectCacheService).getCachedProjectsList();
        verify(projectCacheService, never()).cacheProjectsList(any());
    }

    private Project createTestProject(String name, String slug) {
        Project project = new Project();
        project.setName(name);
        project.setSlug(slug);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setDescription("Test description");
        project.setFeatures(new ArrayList<>());
        project.setTechnologies(new ArrayList<>());
        project.setContributors(new ArrayList<>());
        project.setProjectImages(new ArrayList<>());
        project.setCreatedAt(new Date());
        project.setLastModifiedAt(new Date());
        return project;
    }
}
