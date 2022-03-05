package com.intervale.statistics.exception;

import com.intervale.statistics.response.BookResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler{

    @ExceptionHandler(BookException.class)
    public ResponseEntity<BookResponse> handleExceptionBookCreat(BookException e) {
        return ResponseEntity.badRequest().body(new BookResponse(e.getMessage()));
    }


}
