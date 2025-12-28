package org.noisevisionproductions.noisevision.intergration.project;

import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import org.noisevisionproductions.noisevision.cache.service.project.ProjectCacheService;
import org.noisevisionproductions.noisevision.intergration.config.KafkaTestConfig;
import org.noisevisionproductions.noisevision.intergration.config.TestRedisConfiguration;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectDTO;
import org.noisevisionproductions.noisevision.projectsManagement.dto.ProjectImageDTO;
import org.noisevisionproductions.noisevision.projectsManagement.model.ImageFromProject;
import org.noisevisionproductions.noisevision.projectsManagement.model.Project;
import org.noisevisionproductions.noisevision.projectsManagement.model.ProjectStatus;
import org.noisevisionproductions.noisevision.projectsManagement.repository.ProjectRepository;
import org.noisevisionproductions.noisevision.projectsManagement.service.FileStorageService;
import org.noisevisionproductions.noisevision.projectsManagement.service.ProjectImageService;
import org.noisevisionproductions.noisevision.projectsManagement.service.mainProjectService.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestRedisConfiguration.class, KafkaTestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectImageServiceIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @MockitoBean
    private ProjectCacheService projectCacheService;

    @MockitoBean
    private FileStorageService fileStorageService;

    @Autowired
    private ProjectService projectService;

    @MockitoSpyBean
    private ProjectImageService projectImageService;

    private Project testProject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        projectRepository.deleteAll();

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("Test Integration Project");
        projectDTO.setDescription("Test Description");
        projectDTO.setSlug("test-integration-project");
        projectDTO.setStatus(ProjectStatus.IN_PROGRESS);
        projectDTO.setFeatures(new ArrayList<>());
        projectDTO.setTechnologies(new ArrayList<>());
        projectDTO.setContributors(new ArrayList<>());

        testProject = projectService.createProject(projectDTO);
        clearInvocations(projectCacheService);
    }

    @Test
    @Transactional
    @Order(1)
    void shouldAddImageToProjectAndCacheIt() {
        ProjectImageDTO imageDTO = new ProjectImageDTO();
        String TEST_IMAGE_PATH = "/images/test-integration.jpg";
        imageDTO.setImageUrl(TEST_IMAGE_PATH);
        imageDTO.setCaption("Test Caption");

        ImageFromProject addedImage = projectImageService.addImageToProject(testProject.getId(), imageDTO);

        Project updatedProject = projectRepository.findById(testProject.getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        assertThat(addedImage).isNotNull();
        assertThat(addedImage.getImageUrl()).isEqualTo(TEST_IMAGE_PATH);
        assertThat(addedImage.getCaption()).isEqualTo("Test Caption");

        assertThat(updatedProject.getProjectImages())
                .isNotEmpty()
                .hasSize(1)
                .extracting(ImageFromProject::getImageUrl)
                .containsExactly(TEST_IMAGE_PATH);

        verify(projectCacheService, times(2)).cache(eq(updatedProject.getId()), any(Project.class));
    }
}