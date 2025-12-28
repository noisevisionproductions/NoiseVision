package org.noisevisionproductions.noisevision.cache.model.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.noisevisionproductions.noisevision.projectsManagement.model.ImageFromProject;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CacheableImage implements Serializable {
    private Long id;
    private String imageUrl;
    private String caption;

    public ImageFromProject toEntity() {
        ImageFromProject image = new ImageFromProject();
        image.setId(id);
        image.setImageUrl(imageUrl);
        image.setCaption(caption);
        image.setProject(image.getProject());
        return image;
    }

    public static CacheableImage fromImage(ImageFromProject image) {
        if (image == null) return null;

        CacheableImage cacheable = new CacheableImage();
        cacheable.setId(image.getId());
        cacheable.setImageUrl(image.getImageUrl());
        cacheable.setCaption(image.getCaption());
        return cacheable;
    }
}