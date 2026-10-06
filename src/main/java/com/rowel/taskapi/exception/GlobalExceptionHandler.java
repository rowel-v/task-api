package com.rowel.taskapi.exception;

import com.rowel.taskapi.dto.response.ApiResponse;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleTaskNotFound(
    TaskNotFoundException ex
  ) {
    return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
  }

  // @Valid failed on the request body
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(
    MethodArgumentNotValidException ex
  ) {
    String message = ex
      .getBindingResult()
      .getFieldErrors()
      .stream()
      .map(error -> error.getDefaultMessage())
      .collect(Collectors.joining(", "));

    return ResponseEntity.badRequest().body(ApiResponse.error(message));
  }

  // body can't be parsed, e.g. a date without a timezone for Instant
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnreadable(
    HttpMessageNotReadableException ex
  ) {
    return ResponseEntity.badRequest().body(
      ApiResponse.error("Invalid request body.")
    );
  }
}
