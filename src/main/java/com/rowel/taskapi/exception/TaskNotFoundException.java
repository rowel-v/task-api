package com.rowel.taskapi.exception;

import java.util.List;

public class TaskNotFoundException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TaskNotFoundException(Long id) {
    super("Task not found with id: " + id);
  }

  public TaskNotFoundException(List<Long> ids) {
    super("Tasks not found: " + ids);
  }
}
