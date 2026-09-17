package com.rowel.taskapi.dto.request;

import java.time.Instant;

import com.rowel.taskapi.model.TaskPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class CreateTaskRequest {

	@NotBlank
	@Size(max = 255)
	String name;

	@Size(max = 2000)
	String description;

	TaskPriority priority;

	Instant dueDate;

}