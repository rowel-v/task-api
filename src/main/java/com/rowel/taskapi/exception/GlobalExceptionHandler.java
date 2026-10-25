package com.rowel.taskapi.exception;

import com.rowel.taskapi.dto.common.ApiResponse;
import com.rowel.taskapi.exception.task.TaskAlreadyCompletedException;
import com.rowel.taskapi.exception.task.TaskNotCompletedException;
import com.rowel.taskapi.exception.task.TaskNotFoundException;
import com.rowel.taskapi.exception.task.InvalidTaskIdsException;
import com.rowel.taskapi.exception.user.UserAlreadyExistsException;
import com.rowel.taskapi.exception.user.UserNotFoundException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // Thrown by bulk task operations for an empty list or invalid task IDs.
  @ExceptionHandler(InvalidTaskIdsException.class)
  public ResponseEntity<ApiResponse<Void>> handleInvalidTaskIds(InvalidTaskIdsException ex) {
    return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
  }

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

  // Thrown by task mutations when a requested task is missing or belongs to another user.
  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleTaskNotFound(
    TaskNotFoundException ex
  ) {
    return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
  }

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

  // Thrown by TaskService when updating or starting an already completed task.
  @ExceptionHandler(TaskAlreadyCompletedException.class)
  public ResponseEntity<ApiResponse<Void>> handleCompleted(
    TaskAlreadyCompletedException ex
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      ApiResponse.error(ex.getMessage())
    );
  }

  // Thrown by TaskService when reopening or archiving a task that is not completed.
  @ExceptionHandler(TaskNotCompletedException.class)
  public ResponseEntity<ApiResponse<Void>> handleNotCompleted(
    TaskNotCompletedException ex
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      ApiResponse.error(ex.getMessage())
    );
  }
}
