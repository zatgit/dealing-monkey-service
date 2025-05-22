package com.dealermonkey.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class DeckExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DeckException> handleIllegalArgument(IllegalArgumentException ex) {
        final DeckException deckException =
                DeckException.builder()
                        .timestamp(Instant.now())
                        .status(BAD_REQUEST.value())
                        .error(BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build();
        return new ResponseEntity<>(deckException, BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DeckException> handleDeserializationError(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        final DeckException deckException =
                DeckException.builder()
                        .timestamp(Instant.now())
                        .status(BAD_REQUEST.value())
                        .error(BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMostSpecificCause().getMessage())
                        .path(request.getRequestURI())
                        .build();
        return new ResponseEntity<>(deckException, BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DeckException> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        final DeckException deckException =
                DeckException.builder()
                        .timestamp(Instant.now())
                        .status(BAD_REQUEST.value())
                        .error(BAD_REQUEST.getReasonPhrase())
                        .message(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                        .path(request.getRequestURI())
                        .build();
        return new ResponseEntity<>(deckException, BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<DeckException> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {
        final HttpStatus status = (HttpStatus) ex.getStatusCode();
        final DeckException deckException =
                DeckException.builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(ex.getReason())
                        .path(request.getRequestURI())
                        .build();
        return new ResponseEntity<>(deckException, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DeckException> handleUnhandledException(Exception ex, HttpServletRequest request) {
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        DeckException deckException = DeckException.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage() != null ? ex.getMessage() : "Unexpected server error")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(deckException, status);
    }
}