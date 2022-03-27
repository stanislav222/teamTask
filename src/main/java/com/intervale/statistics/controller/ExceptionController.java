package com.intervale.statistics.controller;

import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.exception.RateAlfaBankException;
import com.intervale.statistics.response.SimpleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BookException.class)
    public ResponseEntity<SimpleResponse> handleExceptionBookCreate(BookException e) {
        return ResponseEntity.badRequest().body(new SimpleResponse(e.getMessage()));
    }

    @ExceptionHandler(GenerateException.class)
    public ResponseEntity<SimpleResponse> handleExceptionGenerateFile(GenerateException e) {
        return ResponseEntity.badRequest().body(new SimpleResponse(e.getMessage()));
    }

    @ExceptionHandler(RateAlfaBankException.class)
    public ResponseEntity<SimpleResponse> handleRateAlfaBankException(RateAlfaBankException e) {
        return ResponseEntity.badRequest().body(new SimpleResponse(e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<SimpleResponse> handleExceptionBookCreate(RuntimeException e) {
        return ResponseEntity.badRequest().body(new SimpleResponse(e.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handlerConstraintViolationException(ConstraintViolationException e) {

        List<String> errorMessages = new ArrayList<>();
        e.getConstraintViolations().forEach(violation -> errorMessages.add(violation.getMessage()));

        return ResponseEntity.badRequest().body(errorMessages);
    }
}
