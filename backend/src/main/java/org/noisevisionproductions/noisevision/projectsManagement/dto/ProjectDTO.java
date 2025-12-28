package org.noisevisionproductions.noisevision.projectsManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.noisevisionproductions.noisevision.projectsManagement.model.ProjectStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Schema(description = "Reprezentacja projektu w systemie")
public class ProjectDTO {
    @Schema(description = "Unikalny identyfikator projektu")
    private Long id;
    @Schema(description = "Nazwa projektu")
    private String name;
    @Schema(description = "Unikalny slug projektu")
    private String slug;
    @Schema(description = "Szczegółowy opis projektu")
    private String description;
    private String repositoryUrl;
    private ProjectStatus status;
    private Date startDate;
    private Date endDate;
    private List<String> features = new ArrayList<>();
    @Schema(description = "Lista technologii związanych z projektem")
    private List<String> technologies;
    private List<ContributorDTO> contributors = new ArrayList<>();
    @Schema(description = "Lista obrazów użytych w projekcie")
    private List<ProjectImageDTO> projectImages;
}
