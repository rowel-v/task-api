package com.rowel.taskapi.common.exception;

import com.rowel.taskapi.common.dto.ApiResponse;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // Raised by Spring MVC when an @Valid request body fails field validation.
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

  // Raised by Spring MVC for a missing or unreadable request body, such as malformed JSON or an invalid date..
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnreadable(
    HttpMessageNotReadableException ex
  ) {
    return ResponseEntity.badRequest().body(
      ApiResponse.error("Invalid request body.")
    );
  }
}
