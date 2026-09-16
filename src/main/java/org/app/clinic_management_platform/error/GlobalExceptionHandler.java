package org.app.clinic_management_platform.error;

import org.app.clinic_management_platform.patient.PatientNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleValidationException(MethodArgumentNotValidException execption){
        Map<String, String> errors = execption.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (existing,replacement) -> existing
                ));
        return new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                LocalDateTime.now(),
                errors
        );
    }

    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handlePatientNotFound(PatientNotFoundException exception){
        return new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                LocalDateTime.now(),
                null
        );
    }
}
