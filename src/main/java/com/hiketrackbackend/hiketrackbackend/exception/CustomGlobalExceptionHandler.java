package com.hiketrackbackend.hiketrackbackend.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST);
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleJwtExceptionException(JwtException ex) {
        return createBodyMessage(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Object> handleRegistrationException(RegistrationException ex) {
        return createBodyMessage(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return createBodyMessage(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException ex
    ) {
        return createBodyMessage(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        return createBodyMessage(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SpecificationNotFoundException.class)
    public ResponseEntity<Object> handleSpecificationNotFoundException(
            SpecificationNotFoundException ex
    ) {
        return createBodyMessage(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Object> handleInvalidTokenException(InvalidTokenException ex) {
        return createBodyMessage(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<Object> handleEmailSendingException(EmailSendingException ex) {
        return createBodyMessage(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MemoryLimitException.class)
    public ResponseEntity<Object> handleMemoryLimitException(MemoryLimitException ex) {
        return createBodyMessage(ex, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(UserNotConfirmedException.class)
    public ResponseEntity<Object> handleUserNotConfirmedException(UserNotConfirmedException ex) {
        return createBodyMessage(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Object> handleFileUploadException(FileUploadException ex) {
        return createBodyMessage(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<Object> handleEntityAlreadyExistException(
            EntityAlreadyExistException ex
    ) {
        return createBodyMessage(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FileIsEmptyException.class)
    public ResponseEntity<Object> handleFileIsEmptyException(FileIsEmptyException ex) {
        return createBodyMessage(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<Object> handleInvalidIdException(InvalidIdException ex) {
        return createBodyMessage(ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> createBodyMessage(Exception ex, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", ex.getClass().getSimpleName());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError fieldError) {
            String fieldName = fieldError.getField();
            String errorMessage = e.getDefaultMessage();
            return fieldName + " " + errorMessage;
        }
        return e.getDefaultMessage();
    }
}
