package org.noisevisionproductions.noisevision.auth.exceptions;

import org.noisevisionproductions.noisevision.exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler({
            IllegalArgumentException.class,
            EmailAlreadyExistsException.class,
            RegistrationBlockedException.class
    })
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception) {
        String errorKey = mapErrorMessageToKey(exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("error", errorKey);

        if (errorKey.equals("invalidCredentials")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException authenticationException) {
        ErrorResponse errorResponse = new ErrorResponse("error", "invalidCredentials");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException invalidCredentialsException) {
        ErrorResponse errorResponse = new ErrorResponse("error", "invalidCredentials");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    private String mapErrorMessageToKey(String message) {
        if (message == null) return "generic";

        String lowerCaseMessage = message.toLowerCase();

        if (lowerCaseMessage.contains("registration is blocked")) {
            return "registrationBlocked";
        }
        if (lowerCaseMessage.contains("email already exists")) {
            return "emailExists";
        }
        if (lowerCaseMessage.contains("invalid credentials") ||
                lowerCaseMessage.contains("bad credentials") ||
                lowerCaseMessage.contains("user not found")) {
            return "invalidCredentials";
        }
        return "generic";
    }
}