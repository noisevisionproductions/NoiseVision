package org.noisevisionproductions.noisevision.projectsManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Reprezentacja obrazu w projekcie")
public class ProjectImageDTO {
    @Schema(description = "Unikalny identyfikator obrazu")
    private Long id;
    @Schema(description = "URL do obrazu")
    private String imageUrl;
    @Schema(description = "Podpis/opis obrazu")
    private String caption;
}
