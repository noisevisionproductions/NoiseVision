package org.noisevisionproductions.portfolio.projectsManagement.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;

class FileStorageExceptionTest {

    @Test
    void constructor_ShouldCreateException_WithMessage() {
        String errorMessage = "Test error message";

        FileStorageException exception = new FileStorageException(errorMessage);

        assertThat(exception)
                .hasMessage(errorMessage)
                .hasNoCause();
    }

    @Test
    void constructor_ShouldCreateException_WithMessageAndCause() {
        String errorMessage = "Test error message";
        Throwable cause = new RuntimeException("Original cause");

        FileStorageException exception = new FileStorageException(errorMessage, cause);

        assertThat(exception)
                .hasMessage(errorMessage)
                .hasCause(cause);
    }

    @Test
    void class_ShouldHaveCorrectResponseStatus() {
        ResponseStatus annotation = FileStorageException.class.getAnnotation(ResponseStatus.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
