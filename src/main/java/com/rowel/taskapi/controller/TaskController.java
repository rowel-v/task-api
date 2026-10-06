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
      ApiResponse.successful("Tasks All retrieved", tasks)
    );
  }

  @PostMapping
  public ResponseEntity<ApiResponse<TaskResponse>> createTask(
    @Valid @RequestBody CreateTaskRequest req
  ) {
    TaskResponse task = taskService.createTask(req);
    return ResponseEntity.status(201).body(
      ApiResponse.successful("Task created successfully", task)
    );
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
    @PathVariable Long id,
    @Valid @RequestBody UpdateTaskRequest req
  ) {
    TaskResponse task = taskService.updateTask(id, req);
    return ResponseEntity.ok(
      ApiResponse.successful("Task updated successfully", task)
    );
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteTask(
    @Valid @RequestBody DeleteTaskRequest req
  ) {
    taskService.deleteTask(req);

    int count = req.taskIds().size();
    String message =
      count == 1
        ? "Task deleted successfully"
        : String.format("%d tasks deleted successfully", count);

    return ResponseEntity.status(200).body(
      ApiResponse.successful(message, null)
    );
  }

  @PatchMapping("/{id}/{taskStatusAction}")
  public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
    @PathVariable Long id,
    @PathVariable TaskStatusAction taskStatusAction
  ) {
    TaskResponse result = taskService.updateTaskStatus(id, taskStatusAction);

    return ResponseEntity.ok(
      ApiResponse.successful("Task updated successfully", result)
    );
  }
}
