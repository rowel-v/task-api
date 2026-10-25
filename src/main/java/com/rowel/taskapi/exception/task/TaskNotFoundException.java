package com.rowel.taskapi.exception.task;

import java.util.List;

public class TaskNotFoundException extends RuntimeException {

  public TaskNotFoundException(Long id) {
    super("Task not found with id: " + id);
  }

  public TaskNotFoundException(List<Long> ids) {
    super("Tasks not found: " + ids);
  }
}
