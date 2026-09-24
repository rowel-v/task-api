package com.rowel.taskapi.dto.request;

import com.rowel.taskapi.shared.TaskStatusAction;

public record UpdateTaskStatusRequest(
  Long taskId,
  TaskStatusAction taskStatusAction
) {}
