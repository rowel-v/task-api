package com.rowel.taskapi.dto.request;

import com.rowel.taskapi.model.TaskPriority;
import com.rowel.taskapi.model.TaskStatus;
import java.time.Instant;

public record UpdateTaskRequest(
  String name,
  String description,
  TaskStatus status,
  TaskPriority priority,
  Instant dueDate
) {}
