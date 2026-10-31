package com.rowel.taskapi.auth.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rowel.taskapi.auth.exception.UserAlreadyExistsException;
import com.rowel.taskapi.auth.exception.UserNotFoundException;
import com.rowel.taskapi.common.dto.ApiResponse;

@RestControllerAdvice
public class AuthExceptionHandler {

  // Thrown by AuthService.createAccount() when the username is already registered.
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(
    UserAlreadyExistsException ex
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      ApiResponse.error(ex.getMessage())
    );
  }

  // Thrown by TaskService.currentUser() when the authenticated user no longer exists.
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleUserNotFound(
    UserNotFoundException ex
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      ApiResponse.error(ex.getMessage())
    );
  }
}
