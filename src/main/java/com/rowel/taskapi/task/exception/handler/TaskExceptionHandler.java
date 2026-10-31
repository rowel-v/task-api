package com.rowel.taskapi.task.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rowel.taskapi.common.dto.ApiResponse;
import com.rowel.taskapi.task.exception.InvalidTaskIdsException;
import com.rowel.taskapi.task.exception.TaskAlreadyCompletedException;
import com.rowel.taskapi.task.exception.TaskNotCompletedException;
import com.rowel.taskapi.task.exception.TaskNotFoundException;

@RestControllerAdvice
public class TaskExceptionHandler {

  // Thrown by bulk task operations for an empty list or invalid task IDs.
  @ExceptionHandler(InvalidTaskIdsException.class)
  public ResponseEntity<ApiResponse<Void>> handleInvalidTaskIds(
    InvalidTaskIdsException ex
  ) {
    return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
  }

  // Thrown by task mutations when a requested task is missing or belongs to another user.
  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleTaskNotFound(
    TaskNotFoundException ex
  ) {
    return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
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
