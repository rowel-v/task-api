package com.rowel.taskapi.task.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;

public record UserWithTasksResponse(
  @Schema(requiredMode = RequiredMode.REQUIRED) String username,

  @Schema(nullable = true) String displayName,

  @Schema(nullable = true) String timeZone,

  @Schema(nullable = true) String bio,

  @Schema(requiredMode = RequiredMode.REQUIRED) List<TaskResponse> tasks
) {}
