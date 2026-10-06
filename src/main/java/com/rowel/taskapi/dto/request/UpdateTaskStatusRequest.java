package com.rowel.taskapi.dto.request;

import com.rowel.taskapi.shared.TaskStatusAction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateTaskStatusRequest(
  @NotNull(message = "{task.id.notnull}")
  @Positive(message = "{task.id.positive}")
  Long taskId,

  @NotNull(message = "{task.statusaction.notnull}")
  TaskStatusAction taskStatusAction
) {}