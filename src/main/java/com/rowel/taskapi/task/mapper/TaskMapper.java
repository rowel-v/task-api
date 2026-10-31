package com.rowel.taskapi.task.mapper;

import com.rowel.taskapi.auth.model.TaskUser;
import com.rowel.taskapi.task.dto.request.CreateTaskRequest;
import com.rowel.taskapi.task.dto.request.UpdateTaskRequest;
import com.rowel.taskapi.task.dto.response.TaskResponse;
import com.rowel.taskapi.task.dto.response.UserWithTasksResponse;
import com.rowel.taskapi.task.model.Task;
import com.rowel.taskapi.task.model.TaskStatus;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

  // convert CreateTaskRequest DTO to Task Entity
  public Task requestToTask(CreateTaskRequest req, TaskUser user) {
    return Task.builder()
      .user(user)
      .name(req.name().trim())
      .description(req.description() == null ? null : req.description().trim())
      .status(TaskStatus.PENDING)
      .priority(req.priority())
      .dueDate(req.dueDate())
      .build();
  }

  // convert Task entity to TaskResponse DTO
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
      task.getCompletedAt(),
      task.isArchived(),
      task.getArchivedAt()
    );
  }

  // Maps the user and their tasks to a UserWithTasksResponse DTO.
  public UserWithTasksResponse tasksToResponse(
    TaskUser user,
    List<Task> tasks
  ) {
    return new UserWithTasksResponse(
      user.getUsername(),
      user.getDisplayName(),
      tasks.stream().map(this::taskToResponse).toList()
    );
  }

  // Updates the task using non-null request fields.
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
