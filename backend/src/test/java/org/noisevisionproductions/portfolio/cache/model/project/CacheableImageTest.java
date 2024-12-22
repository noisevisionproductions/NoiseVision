package org.noisevisionproductions.portfolio.cache.model.project;

import org.junit.jupiter.api.Test;
import org.noisevisionproductions.portfolio.projectsManagement.model.ImageFromProject;

import static org.junit.jupiter.api.Assertions.*;

class CacheableImageTest {

    @Test
    void shouldCorrectlyConvertFromImageToCacheableImage() {
        ImageFromProject image = new ImageFromProject();
        image.setImageUrl("test-image.jpg");
        image.setCaption("Test Caption");

        CacheableImage cacheableImage = CacheableImage.fromImage(image);

        assertNotNull(cacheableImage);
        assertEquals("test-image.jpg", cacheableImage.getImageUrl());
        assertEquals("Test Caption", cacheableImage.getCaption());
    }

    @Test
    void shouldCorrectlyConvertCacheableImageToImage() {
        CacheableImage cacheableImage = new CacheableImage();
        cacheableImage.setId(2L);
        cacheableImage.setImageUrl("another-image.png");
        cacheableImage.setCaption("Another Caption");

        ImageFromProject image = cacheableImage.toEntity();

        assertNotNull(image);
        assertEquals(2L, image.getId());
        assertEquals("another-image.png", image.getImageUrl());
        assertEquals("Another Caption", image.getCaption());
    }

    @Test
    void shouldCreateImageWithEmptyFields() {
        CacheableImage cacheableImage = new CacheableImage();

        ImageFromProject image = cacheableImage.toEntity();

        assertNotNull(image);
        assertNull(image.getId());
        assertNull(image.getImageUrl());
        assertNull(image.getCaption());
    }

    @Test
    void shouldCorrectlyUseAllArgsConstructor() {
        Long id = 3L;
        String imageUrl = "profile.jpg";
        String caption = "Profile Picture";

        CacheableImage cacheableImage = new CacheableImage(id, imageUrl, caption);

        assertEquals(id, cacheableImage.getId());
        assertEquals(imageUrl, cacheableImage.getImageUrl());
        assertEquals(caption, cacheableImage.getCaption());
    }

    @Test
    void shouldHandlePartiallyFilledImage() {
        ImageFromProject image = new ImageFromProject();
        image.setImageUrl("partial-image.jpg");

        CacheableImage cacheableImage = CacheableImage.fromImage(image);

        assertNotNull(cacheableImage);
        assertEquals("partial-image.jpg", cacheableImage.getImageUrl());
        assertNull(cacheableImage.getCaption());
    }

    @Test
    void shouldRetainDataTypesAfterConversion() {
        String imageUrl = "test.jpg";
        String caption = "Test";
        CacheableImage cacheableImage = new CacheableImage();
        cacheableImage.setImageUrl(imageUrl);
        cacheableImage.setCaption(caption);

        ImageFromProject image = cacheableImage.toEntity();
        CacheableImage convertedBack = CacheableImage.fromImage(image);

        assertEquals(imageUrl, convertedBack.getImageUrl());
        assertEquals(caption, convertedBack.getCaption());
        assertEquals(cacheableImage.getImageUrl(), convertedBack.getImageUrl());
        assertEquals(cacheableImage.getCaption(), convertedBack.getCaption());
    }
}