package org.noisevisionproductions.noisevision.intergration.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.noisevisionproductions.noisevision.cache.service.project.ProjectCacheService;
import org.noisevisionproductions.noisevision.intergration.config.BaseIntegrationTest;
import org.noisevisionproductions.noisevision.intergration.config.KafkaTestConfig;
import org.noisevisionproductions.noisevision.intergration.config.TestRedisConfiguration;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectDTO;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectImageDTO;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.model.ProjectStatus;
import org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestRedisConfiguration.class, KafkaTestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectCreationIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectCacheService projectCacheService;

    private ProjectDTO createTestProjectDTO() {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("Test Integration Project");
        projectDTO.setDescription("Test Description");
        projectDTO.setSlug("test-integration-project");
        projectDTO.setStatus(ProjectStatus.IN_PROGRESS);
        projectDTO.setFeatures(new ArrayList<>());
        projectDTO.setTechnologies(new ArrayList<>());
        projectDTO.setContributors(new ArrayList<>());

        Date now = new Date();
        projectDTO.setStartDate(now);
        projectDTO.setEndDate(now);

        return projectDTO;
    }

    @Test
    @Order(1)
    @WithMockUser(authorities = "CREATE_PROJECTS")
    void shouldCreateProjectAndCacheIt() throws Exception {
        ProjectDTO projectDTO = createTestProjectDTO();
        String projectJson = objectMapper.writeValueAsString(projectDTO);

        MvcResult result = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(projectDTO.getName()))
                .andExpect(jsonPath("$.slug").value(projectDTO.getSlug()))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ProjectDTO createdProjectDTO = objectMapper.readValue(responseJson, ProjectDTO.class);

        Optional<Project> savedProject = projectRepository.findById(createdProjectDTO.getId());
        assertThat(savedProject).isPresent();
        assertThat(savedProject.get().getName()).isEqualTo(projectDTO.getName());

        Project cachedProject = projectService.getProjectById(createdProjectDTO.getId());
        assertThat(cachedProject).isNotNull();
        assertThat(cachedProject.getName()).isEqualTo(projectDTO.getName());
    }

    @Test
    @Order(2)
    void shouldNotCreateProjectWithoutAuthentication() throws Exception {
        ProjectDTO projectDTO = createTestProjectDTO();
        String projectJson = objectMapper.writeValueAsString(projectDTO);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    @WithMockUser(username = "test")
    void shouldNotCreateProjectWithoutProperAuthority() throws Exception {
        ProjectDTO projectDTO = createTestProjectDTO();
        String projectJson = objectMapper.writeValueAsString(projectDTO);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isForbidden())
                .andDo(result -> {
                    System.out.println("Response: " + result.getResponse().getContentAsString());
                });
    }

    @Test
    @Order(4)
    @WithMockUser(authorities = "CREATE_PROJECTS")
    void shouldValidateProjectData() throws Exception {
        ProjectDTO invalidProject = new ProjectDTO();
        String projectJson = objectMapper.writeValueAsString(invalidProject);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @WithMockUser(username = "test", authorities = {"CREATE_PROJECTS"})
    void shouldCreateAndRetrieveProject() throws Exception {
        ProjectDTO projectDTO = createTestProjectDTO();
        String projectJson = objectMapper.writeValueAsString(projectDTO);

        MvcResult createResult = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        ProjectDTO createdProject = objectMapper.readValue(responseJson, ProjectDTO.class);
        assertThat(createdProject.getId()).isNotNull();
        assertThat(createdProject.getName()).isEqualTo(projectDTO.getName());

        MvcResult getResult = mockMvc.perform(get("/api/projects/slug/{slug}", createdProject.getSlug()))
                .andExpect(status().isOk())
                .andReturn();

        ProjectDTO retrievedProject = objectMapper.readValue(
                getResult.getResponse().getContentAsString(),
                ProjectDTO.class
        );

        assertThat(retrievedProject)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "lastModifiedAt", "startDate", "endDate")
                .isEqualTo(createdProject);

        assertThat(retrievedProject.getStartDate()).isNotNull();
        assertThat(retrievedProject.getEndDate()).isNotNull();

        Project cachedProject = projectCacheService.get(createdProject.getId());
        assertThat(cachedProject).isNotNull();
        assertThat(cachedProject.getName()).isEqualTo(projectDTO.getName());
    }

    @Test
    @Order(6)
    @WithMockUser(username = "test", authorities = {"CREATE_PROJECTS", "EDIT_PROJECTS"})
    void shouldUpdateExistingProject() throws Exception {
        ProjectDTO projectDTO = createTestProjectDTO();
        String projectJson = objectMapper.writeValueAsString(projectDTO);

        MvcResult createResult = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isCreated())
                .andReturn();

        ProjectDTO createdProject = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProjectDTO.class
        );

        createdProject.setName("Updated Project Name");
        createdProject.setDescription("Updated Description");
        createdProject.setStatus(ProjectStatus.COMPLETED);
        String updatedJson = objectMapper.writeValueAsString(createdProject);

        MvcResult updateResult = mockMvc.perform(put("/api/projects/{id}", createdProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andReturn();

        ProjectDTO updatedProject = objectMapper.readValue(
                updateResult.getResponse().getContentAsString(),
                ProjectDTO.class
        );

        assertThat(updatedProject.getName()).isEqualTo("Updated Project Name");
        assertThat(updatedProject.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedProject.getStatus()).isEqualTo(ProjectStatus.COMPLETED);

        Project cachedProject = projectCacheService.get(updatedProject.getId());
        assertThat(cachedProject).isNotNull();
        assertThat(cachedProject.getName()).isEqualTo("Updated Project Name");
        assertThat(cachedProject.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
    }

    @Test
    @Order(7)
    @WithMockUser(username = "test", authorities = {"CREATE_PROJECTS", "EDIT_PROJECTS", "DELETE_PROJECTS"})
    void shouldUpdateProjectWithImages() throws Exception {
        ProjectDTO projectDTO = createTestProjectDTO();
        String projectJson = objectMapper.writeValueAsString(projectDTO);

        MvcResult createResult = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isCreated())
                .andReturn();

        ProjectDTO createdProject = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProjectDTO.class
        );

        ProjectImageDTO imageDTO = new ProjectImageDTO();
        imageDTO.setImageUrl("/images/test-image.jpg");
        imageDTO.setCaption("Test Caption");

        MvcResult addImageResult = mockMvc.perform(post("/api/projects/{id}/images", createdProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(imageDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        ProjectImageDTO savedImage = objectMapper.readValue(
                addImageResult.getResponse().getContentAsString(),
                ProjectImageDTO.class
        );

        assertThat(savedImage.getId()).isNotNull();
        assertThat(savedImage.getImageUrl()).isEqualTo("/images/test-image.jpg");

        Project projectFromDb = projectRepository.findById(createdProject.getId())
                .orElseThrow(() -> new AssertionError("Project not found in database"));

        assertThat(projectFromDb.getProjectImages()).hasSize(1);
        assertThat(projectFromDb.getProjectImages().getFirst().getId()).isNotNull();

        MvcResult getProjectResult = mockMvc.perform(get("/api/projects/{id}", createdProject.getId()))
                .andExpect(status().isOk())
                .andReturn();

        ProjectDTO updatedProject = objectMapper.readValue(
                getProjectResult.getResponse().getContentAsString(),
                ProjectDTO.class
        );

        assertThat(updatedProject.getProjectImages()).hasSize(1);
        assertThat(updatedProject.getProjectImages().getFirst().getImageUrl())
                .isEqualTo("/images/test-image.jpg");
        assertThat(updatedProject.getProjectImages().getFirst().getId())
                .isEqualTo(savedImage.getId());

        mockMvc.perform(delete("/api/projects/{projectId}/images/{imageId}",
                        createdProject.getId(), savedImage.getId()))
                .andExpect(status().isNoContent());

        MvcResult verifyResult = mockMvc.perform(get("/api/projects/{id}", createdProject.getId()))
                .andExpect(status().isOk())
                .andReturn();

        ProjectDTO finalProject = objectMapper.readValue(
                verifyResult.getResponse().getContentAsString(),
                ProjectDTO.class
        );
        assertThat(finalProject.getProjectImages()).isEmpty();

        Project cachedProject = projectCacheService.get(finalProject.getId());
        assertThat(cachedProject).isNotNull();
        assertThat(cachedProject.getProjectImages()).isEmpty();
    }

    @Test
    @Order(7)
    @WithMockUser(username = "test", authorities = {"CREATE_PROJECTS", "DELETE_PROJECTS"})
    void shouldDeleteProject() throws Exception {
        ProjectDTO projectDTO = createTestProjectDTO();
        String projectJson = objectMapper.writeValueAsString(projectDTO);

        MvcResult createResult = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isCreated())
                .andReturn();

        ProjectDTO createdProject = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProjectDTO.class
        );

        mockMvc.perform(delete("/api/projects/{id}", createdProject.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/projects/{id}", createdProject.getId()))
                .andExpect(status().isNotFound());

        Project cachedProject = projectCacheService.get(createdProject.getId());
        assertThat(cachedProject).isNull();

        MvcResult listResult = mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andReturn();

        List<ProjectDTO> projects = objectMapper.readValue(
                listResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );
        assertThat(projects).extracting(ProjectDTO::getId)
                .doesNotContain(createdProject.getId());
    }
}

