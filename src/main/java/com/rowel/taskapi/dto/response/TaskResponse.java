package com.rowel.taskapi.dto.response;

import com.rowel.taskapi.model.TaskPriority;
import com.rowel.taskapi.model.TaskStatus;
import java.time.Instant;

public record TaskResponse(
  Long id,
  String name,
  String description,
  TaskStatus status,
  TaskPriority priority,
  Instant createdAt,
  Instant updatedAt,
  Instant dueDate,
  Instant completedAt
) {}
