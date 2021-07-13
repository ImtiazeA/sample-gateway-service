package com.musala.test.samplegatewayservice.config.exception;

import com.musala.test.samplegatewayservice.controllers.OperationNotAllowedException;
import com.musala.test.samplegatewayservice.dtos.error.ErrorResponseDTO;
import com.musala.test.samplegatewayservice.dtos.error.ErrorDetails;
import com.musala.test.samplegatewayservice.services.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ApplicationControllerAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Object> onConstraintValidationException(ConstraintViolationException e) {

        ErrorResponseDTO error = new ErrorResponseDTO();

        error.setMessage("Bad Request");

        List<ErrorDetails> violations = new ArrayList<>();
        error.setErrors(violations);

        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            violations.add(new ErrorDetails(violation.getPropertyPath().toString(), violation.getMessage()));
        }

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Object> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setMessage("Bad Request");

        List<ErrorDetails> errors = new ArrayList<>();
        error.setErrors(errors);

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(new ErrorDetails(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OperationNotAllowedException.class)
    ResponseEntity<Object> onOperationNotAllowedException(OperationNotAllowedException e) {

        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setMessage("Bad Request");

        List<ErrorDetails> errors = new ArrayList<>();
        error.setErrors(errors);

        errors.add(new ErrorDetails(null, e.getMessage()));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<Object> onEntityNotFoundException(EntityNotFoundException e) {

        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setMessage("Bad Request");

        List<ErrorDetails> errors = new ArrayList<>();
        error.setErrors(errors);

        errors.add(new ErrorDetails(null, e.getMessage()));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

}
