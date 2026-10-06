package com.rowel.taskapi.dto.request;

import com.rowel.taskapi.model.TaskPriority;
import com.rowel.taskapi.model.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateTaskRequest(
  @NotBlank(message = "{task.name.notblank}")
  @Size(max = 255, message = "{task.name.size.invalid}")
  String name,
  
  @NotBlank(message = "{task.description.notblank}")
  @Size(min = 1, max = 2000, message = "{task.description.size.invalid}")
  String description,
  
  @NotNull (message = "{task.status.notnull}")
  TaskStatus status,
  
  @NotNull (message = "{task.priority.notnull}")
  TaskPriority priority,
  
  @NotBlank(message = "{task.description.notblank}")
  @Size(min = 1, max = 2000, message = "{task.description.size.invalid}")
  Instant dueDate
) {}
