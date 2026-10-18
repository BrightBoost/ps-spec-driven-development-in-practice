package com.example.obligations.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.obligations.domain.InvalidObligationException;
import com.example.obligations.domain.ObligationNotFoundException;

/** RFC 9457 Problem Details for every error (NFR-004, constitution principle II). */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidObligationException.class)
    ResponseEntity<ProblemDetail> invalid(InvalidObligationException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problem.setTitle("Invalid obligation");
        problem.setProperty("field", e.field());
        return respond(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ProblemDetail> unreadable(HttpMessageNotReadableException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request body is not valid JSON");
        problem.setTitle("Invalid obligation");
        return respond(problem);
    }

    @ExceptionHandler(ObligationNotFoundException.class)
    ResponseEntity<ProblemDetail> notFound(ObligationNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problem.setTitle("Obligation not found");
        return respond(problem);
    }

    private static ResponseEntity<ProblemDetail> respond(ProblemDetail problem) {
        return ResponseEntity.status(problem.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
