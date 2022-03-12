package com.intervale.statistics.controller;

import com.intervale.statistics.exception.BookException;
import com.intervale.statistics.exception.GenerateException;
import com.intervale.statistics.response.BookResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BookException.class)
    public ResponseEntity<BookResponse> handleExceptionBookCreate(BookException e) {
        return ResponseEntity.badRequest().body(new BookResponse(e.getMessage()));
    }

    @ExceptionHandler(GenerateException.class)
    public ResponseEntity<BookResponse> handleExceptionGenerateFile(GenerateException e) {
        return ResponseEntity.badRequest().body(new BookResponse(e.getMessage()));
    }


}
