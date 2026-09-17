package com.rowel.taskapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rowel.taskapi.dto.request.CreateTaskRequest;
import com.rowel.taskapi.dto.request.UpdateTaskRequest;
import com.rowel.taskapi.dto.response.ApiResponse;
import com.rowel.taskapi.dto.response.TaskResponse;
import com.rowel.taskapi.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController @RequestMapping("/tasks")
public class TaskController {
	
	private final TaskService taskService;
	
	@PostMapping
	public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest req) {
		TaskResponse task = taskService.createTask(req);
		return ResponseEntity.status(201).body(ApiResponse.success("Task created successfully", task));
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest req) {
		TaskResponse task = taskService.updateTask(id, req);
		return ResponseEntity.ok(ApiResponse.success("Task updated successfully", task));
	}

}
