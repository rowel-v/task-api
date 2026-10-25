package com.rowel.taskapi.exception.task;

public class TaskAlreadyCompletedException extends RuntimeException {

  public TaskAlreadyCompletedException() {
    super("Task is already completed and can't be updated.");
  }
}
