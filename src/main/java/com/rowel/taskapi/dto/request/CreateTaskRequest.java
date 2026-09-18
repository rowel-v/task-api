package com.rowel.taskapi.dto.request;

import com.rowel.taskapi.model.TaskPriority;
import java.time.Instant;

public record CreateTaskRequest(
  String name,
  String description,
  TaskPriority priority,
  Instant dueDate
) {}
