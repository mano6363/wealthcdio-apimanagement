package com.example.banking.exception;

import com.example.banking.dto.Responses.ErrorResponse;
import com.example.banking.exception.BankingExceptions.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(AccountNotFoundException e) {
        return response(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    ResponseEntity<ErrorResponse> insufficient(InsufficientFundsException e) {
        return response(HttpStatus.UNPROCESSABLE_ENTITY, e);
    }

    @ExceptionHandler({InvalidAmountException.class, DuplicateAccountException.class,
            IllegalArgumentException.class})
    ResponseEntity<ErrorResponse> badRequest(RuntimeException e) {
        return response(HttpStatus.BAD_REQUEST, e);
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, RuntimeException e) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), status.getReasonPhrase(), e.getMessage()));
    }
}
