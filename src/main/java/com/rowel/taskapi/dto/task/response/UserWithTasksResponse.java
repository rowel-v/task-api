package com.rowel.taskapi.dto.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;

public record UserWithTasksResponse(
  
  @Schema(requiredMode = RequiredMode.REQUIRED)
  String username,
  
  @Schema(requiredMode = RequiredMode.REQUIRED)
  String displayName,
  
  @Schema(requiredMode = RequiredMode.REQUIRED)
  List<TaskResponse> tasks
  
) {}
