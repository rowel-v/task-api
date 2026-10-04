package com.rowel.taskapi.controller;

import com.rowel.taskapi.dto.request.CreateTaskRequest;
import com.rowel.taskapi.dto.request.DeleteTaskRequest;
import com.rowel.taskapi.dto.request.UpdateTaskRequest;
import com.rowel.taskapi.dto.response.ApiResponse;
import com.rowel.taskapi.dto.response.TaskResponse;
import com.rowel.taskapi.service.TaskService;
import com.rowel.taskapi.shared.TaskStatusAction;
import jakarta.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService; 
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks() {
    List<TaskResponse> tasks = taskService.getAllTasks();
    return ResponseEntity.ok(
      ApiResponse.successfull("Tasks All retrieved", tasks)
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TaskResponse>> getTask(
    @PathVariable Long id
  ) {
    TaskResponse task = taskService.getTask(id);
    return ResponseEntity.ok(
      ApiResponse.successfull("Task retrieve successfully", task)
    );
  }

  @PostMapping
  public ResponseEntity<ApiResponse<TaskResponse>> createTask(
    @Valid @RequestBody CreateTaskRequest req
  ) {
    TaskResponse task = taskService.createTask(req);
    return ResponseEntity.status(201).body(
      ApiResponse.successfull("Task created successfully", task)
    );
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
    @PathVariable("id") Long id,
    @Valid @RequestBody UpdateTaskRequest req
  ) {
    TaskResponse task = taskService.updateTask(id, req);
    return ResponseEntity.ok(
      ApiResponse.successfull("Task updated successfully", task)
    );
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteTask(
    @Valid  @RequestBody DeleteTaskRequest req
  ) {
    taskService.deleteTask(req);
    return ResponseEntity.noContent().build(); // 204
  }

  @PatchMapping("/{id}/{taskStatusAction}")
  public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
    @PathVariable Long id,
    @PathVariable TaskStatusAction taskStatusAction
  ) {
    TaskResponse result = taskService.updateTaskStatus(id, taskStatusAction);

    return ResponseEntity.ok(
      ApiResponse.successfull("Task updated successfully", result)
    );
  }
}
