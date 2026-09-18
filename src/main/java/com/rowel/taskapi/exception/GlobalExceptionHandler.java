package com.rowel.taskapi.exception;

import com.rowel.taskapi.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
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
}
