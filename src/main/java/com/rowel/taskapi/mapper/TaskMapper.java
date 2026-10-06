package com.rowel.taskapi.mapper;

import com.rowel.taskapi.dto.request.CreateTaskRequest;
import com.rowel.taskapi.dto.request.UpdateTaskRequest;
import com.rowel.taskapi.dto.response.TaskResponse;
import com.rowel.taskapi.model.Task;
import com.rowel.taskapi.model.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

  public Task requestToTask(CreateTaskRequest req) {
    return Task.builder()
      .name(req.name().trim())
      .description(req.description().trim())
      .status(TaskStatus.PENDING)
      .priority(req.priority())
      .dueDate(req.dueDate())
      .build();
  }

  public TaskResponse taskToResponse(Task task) {
    return new TaskResponse(
      task.getId(),
      task.getName(),
      task.getDescription(),
      task.getStatus(),
      task.getPriority(),
      task.getCreatedAt(),
      task.getUpdatedAt(),
      task.getDueDate(),
      task.getCompletedAt()
    );
  }

  public void updateTaskFromRequest(UpdateTaskRequest req, Task task) {
    if (req.name() != null) {
      task.setName(req.name().trim());
    }
    if (req.description() != null) {
      task.setDescription(req.description().trim());
    }
    if (req.status() != null) {
      task.setStatus(req.status());
    }
    if (req.priority() != null) {
      task.setPriority(req.priority());
    }
    if (req.dueDate() != null) {
      task.setDueDate(req.dueDate());
    }
  }
}
