package com.rowel.taskapi.dto.request;

import com.rowel.taskapi.model.TaskPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record CreateTaskRequest(
  
  @NotBlank(message = "{task.name.notblank}")
  @Size(max = 255, message = "{task.name.size.invalid}")
  String name,

  @Size(max = 2000, message = "{task.description.size.invalid}")
  String description,

  @NotNull(message = "{task.priority.notnull}")
  TaskPriority priority,

  @NotNull(message = "{task.duedate.notnull}")
  @Future(message = "{task.duedate.notfuture}") 
  Instant dueDate
) {}
