package com.rowel.taskapi.task.exception;

public class InvalidTaskIdsException extends RuntimeException {
  public InvalidTaskIdsException() {
    super("Select at least one task using positive task IDs.");
  }
}
