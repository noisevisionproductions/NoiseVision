package org.noisevisionproductions.noisevision.projectsManagement.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionHandler {
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<Void> handleProjectNotFound(ProjectNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }
}
