package com.rowel.taskapi.dto.request;

import java.time.Instant;

import com.rowel.taskapi.model.TaskPriority;
import com.rowel.taskapi.model.TaskStatus;

import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class UpdateTaskRequest {

	@Size(max = 255)
	String name;

	@Size(max = 2000)
	String description;

	TaskStatus status;

	TaskPriority priority;

	Instant dueDate;

}