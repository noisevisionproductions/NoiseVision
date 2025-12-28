package org.noisevisionproductions.noisevision.projectsManagement.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "file")
@Component
@Getter
@Setter
public class FileStorageProperties {
    private String uploadDir;
}
