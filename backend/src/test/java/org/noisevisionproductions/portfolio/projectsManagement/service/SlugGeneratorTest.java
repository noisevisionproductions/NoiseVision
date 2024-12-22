package org.noisevisionproductions.portfolio.projectsManagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.projectsManagement.repository.ProjectRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlugGeneratorTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private SlugGenerator slugGenerator;

    @Test
    void generateUniqueSlug_ShouldGenerateBasicSlug_WhenTitleIsSimple() {
        String title = "Test Project";
        when(projectRepository.existsBySlug("test-project")).thenReturn(false);

        String result = slugGenerator.generateUniqueSlug(title);

        assertThat(result).isEqualTo("test-project");
        verify(projectRepository).existsBySlug("test-project");
    }

    @Test
    void generateUniqueSlug_ShouldRemoveSpecialCharacters() {
        String title = "Test!@#$%^&* Project";
        when(projectRepository.existsBySlug("test-project")).thenReturn(false);

        String result = slugGenerator.generateUniqueSlug(title);

        assertThat(result).isEqualTo("test-project");
    }

    @Test
    void generateUniqueSlug_ShouldHandleMultipleSpaces() {
        String title = "Test      Project     Name";
        when(projectRepository.existsBySlug("test-project-name")).thenReturn(false);

        String result = slugGenerator.generateUniqueSlug(title);

        assertThat(result).isEqualTo("test-project-name");
    }

    @Test
    void generateUniqueSlug_ShouldAddCounter_WhenSlugExists() {
        String title = "Test Project";
        when(projectRepository.existsBySlug("test-project")).thenReturn(true);
        when(projectRepository.existsBySlug("test-project-2")).thenReturn(true);
        when(projectRepository.existsBySlug("test-project-3")).thenReturn(false);

        String result = slugGenerator.generateUniqueSlug(title);

        assertThat(result).isEqualTo("test-project-3");
        verify(projectRepository).existsBySlug("test-project");
        verify(projectRepository).existsBySlug("test-project-2");
        verify(projectRepository).existsBySlug("test-project-3");
    }

    @Test
    void generateUniqueSlug_ShouldThrowException_WhenTitleIsNull() {
        assertThatThrownBy(() -> slugGenerator.generateUniqueSlug(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title cannot be null or empty");
    }

    @Test
    void generateUniqueSlug_ShouldThrowException_WhenTitleIsEmpty() {
        assertThatThrownBy(() -> slugGenerator.generateUniqueSlug(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title cannot be null or empty");
    }

    @Test
    void generateUniqueSlug_ShouldThrowException_WhenTitleContainsOnlySpecialCharacters() {
        assertThatThrownBy(() -> slugGenerator.generateUniqueSlug("!@#$%^&*"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Slug cannot be empty after processing");
    }

    @Test
    void generateUniqueSlug_ShouldHandleNonEnglishCharacters() {
        String title = "żółć łódką";
        when(projectRepository.existsBySlug(anyString())).thenReturn(false);

        String result = slugGenerator.generateUniqueSlug(title);

        assertThat(result).isEqualTo("zolc-lodka");
    }

    @Test
    void generateUniqueSlug_ShouldHandleLeadingAndTrailingDashes() {
        String title = "---Test Project---";
        when(projectRepository.existsBySlug("test-project")).thenReturn(false);

        String result = slugGenerator.generateUniqueSlug(title);

        assertThat(result).isEqualTo("test-project");
    }

    @Test
    void generateUniqueSlug_ShouldHandleDoubleDashes() {
        String title = "Test--Project--Name";
        when(projectRepository.existsBySlug("test-project-name")).thenReturn(false);

        String result = slugGenerator.generateUniqueSlug(title);

        assertThat(result).isEqualTo("test-project-name");
    }
}