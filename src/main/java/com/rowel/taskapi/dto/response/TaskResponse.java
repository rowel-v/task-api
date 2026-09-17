package com.rowel.taskapi.dto.response;

import java.time.Instant;

import com.rowel.taskapi.model.TaskPriority;
import com.rowel.taskapi.model.TaskStatus;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskResponse {
    Long id;
    String name;
    String description;
    TaskStatus status;
    TaskPriority priority;
    Instant createdAt;
    Instant updatedAt;
    Instant dueDate;
    Instant completedAt;
}