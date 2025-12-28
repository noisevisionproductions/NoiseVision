package org.noisevisionproductions.noisevision.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/errors")
public class ErrorController {

    @GetMapping("/unauthorized")
    public ResponseEntity<Map<String, String>> getUnauthorizedError() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Nie masz uprawnień");
        response.put("code", "UNAUTHORIZED");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }
}
