package com.rowel.taskapi.task.exception;

import java.util.List;

public class TaskNotCompletedException extends RuntimeException {

  public TaskNotCompletedException(List<Long> ids) {
    super("Tasks not completed yet: " + ids);
  }
}
