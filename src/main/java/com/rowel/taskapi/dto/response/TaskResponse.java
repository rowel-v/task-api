package com.rowel.taskapi.dto.response;

import com.rowel.taskapi.model.TaskPriority;
import com.rowel.taskapi.model.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.time.Instant;

public record TaskResponse(
  @Schema(requiredMode = RequiredMode.REQUIRED)
  Long id,
  
  @Schema(requiredMode = RequiredMode.REQUIRED)
  String name,
  
  @Schema (nullable = true)
  String description,
  
  @Schema(requiredMode = RequiredMode.REQUIRED)
  TaskStatus status,
  
  @Schema(requiredMode = RequiredMode.REQUIRED)
  TaskPriority priority,
  
  @Schema(requiredMode = RequiredMode.REQUIRED)
  Instant createdAt,
  
  @Schema(requiredMode = RequiredMode.REQUIRED)
  Instant updatedAt,
  
  @Schema(requiredMode = RequiredMode.REQUIRED)
  Instant dueDate,
  
  @Schema (nullable = true)
  Instant completedAt
) {}
