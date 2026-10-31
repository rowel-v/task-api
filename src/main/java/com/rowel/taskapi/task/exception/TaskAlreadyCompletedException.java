package com.rowel.taskapi.task.exception;

public class TaskAlreadyCompletedException extends RuntimeException {

  public TaskAlreadyCompletedException() {
    super("Task is already completed and can't be updated.");
  }
}
