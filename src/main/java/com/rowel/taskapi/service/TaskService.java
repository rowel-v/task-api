package com.rowel.taskapi.service;

import com.rowel.taskapi.dto.task.request.CreateTaskRequest;
import com.rowel.taskapi.dto.task.request.UpdateTaskRequest;
import com.rowel.taskapi.dto.task.response.TaskResponse;
import com.rowel.taskapi.dto.task.response.UserWithTasksResponse;
import com.rowel.taskapi.exception.task.InvalidTaskIdsException;
import com.rowel.taskapi.exception.task.TaskAlreadyCompletedException;
import com.rowel.taskapi.exception.task.TaskNotCompletedException;
import com.rowel.taskapi.exception.task.TaskNotFoundException;
import com.rowel.taskapi.exception.user.UserNotFoundException;
import com.rowel.taskapi.mapper.TaskMapper;
import com.rowel.taskapi.model.Task;
import com.rowel.taskapi.model.TaskStatus;
import com.rowel.taskapi.model.TaskUser;
import com.rowel.taskapi.repository.TaskRepository;
import com.rowel.taskapi.repository.TaskUserRepository;
import com.rowel.taskapi.security.TaskUserDetails;
import com.rowel.taskapi.shared.TaskStatusAction;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final TaskUserRepository userRepository;
  private final TaskMapper taskMapper;

  public TaskService(
    TaskRepository tRepository,
    TaskUserRepository userRepository,
    TaskMapper tMapper
  ) {
    this.taskRepository = tRepository;
    this.userRepository = userRepository;
    this.taskMapper = tMapper;
  }

  private Long currentUserId() {
    Authentication authentication =
      SecurityContextHolder.getContext().getAuthentication();

    if (
      authentication == null ||
      !authentication.isAuthenticated() ||
      !(authentication.getPrincipal() instanceof TaskUserDetails principal)
    ) {
      throw new AuthenticationCredentialsNotFoundException(
        "Authentication is required"
      );
    }

    return principal.getUserId();
  }

  private TaskUser currentUser() {
    return userRepository
      .findById(currentUserId())
      .orElseThrow(UserNotFoundException::new);
  }

  public UserWithTasksResponse getAllTasks() {
    TaskUser user = currentUser();
    List<Task> tasks = taskRepository.findByUser(user);

    return taskMapper.tasksToResponse(user, tasks);
  }

  public TaskResponse createTask(CreateTaskRequest req) {
    TaskUser user = currentUser();
    Task task = taskMapper.requestToTask(req, user);
    Task saved = taskRepository.save(task);
    return taskMapper.taskToResponse(saved);
  }

  public TaskResponse updateTask(Long id, UpdateTaskRequest req) {
    return taskRepository
      .findByIdAndUser_Id(id, currentUserId())
      .map(task -> {
        // should not update task if already completed
        if (task.getStatus().equals(TaskStatus.COMPLETED)) {
          throw new TaskAlreadyCompletedException();
        }

        taskMapper.updateTaskFromRequest(req, task);

        if (
          req.status() == TaskStatus.COMPLETED && task.getCompletedAt() == null
        ) {
          task.setCompletedAt(Instant.now());
        }

        return taskMapper.taskToResponse(taskRepository.save(task));
      })
      .orElseThrow(() -> new TaskNotFoundException(id));
  }

  private List<Long> validatedTaskIds(List<Long> ids) {
    if (
      ids == null ||
      ids.isEmpty() ||
      ids.stream().anyMatch(id -> id == null || id <= 0)
    ) {
      throw new InvalidTaskIdsException();
    }
    return ids.stream().distinct().toList();
  }

  @SuppressWarnings("null")
  @Transactional
  public int deleteTask(List<Long> tasksIds) {
    tasksIds = validatedTaskIds(tasksIds);
    List<Task> tasks = taskRepository.findAllByIdInAndUser_Id(
      tasksIds,
      currentUserId()
    );

    Set<Long> foundIds = tasks
      .stream()
      .map(Task::getId)
      .collect(Collectors.toSet());

    // ids the client sent that don't exist
    List<Long> missingIds = tasksIds
      .stream()
      .filter(id -> !foundIds.contains(id))
      .toList();

    if (!missingIds.isEmpty()) {
      throw new TaskNotFoundException(missingIds);
    }

    taskRepository.deleteAll(tasks);
    return tasks.size();
  }

  public TaskResponse updateTaskStatus(
    Long taskId,
    TaskStatusAction taskStatusAction
  ) {
    return taskRepository
      .findByIdAndUser_Id(taskId, currentUserId())
      .map(targetTask -> {
        switch (taskStatusAction) {
          case START -> {
            if (targetTask.getStatus() == TaskStatus.COMPLETED) {
              throw new TaskAlreadyCompletedException();
            }
            targetTask.setStatus(TaskStatus.IN_PROGRESS);
          }
          case COMPLETE -> {
            targetTask.setStatus(TaskStatus.COMPLETED);
            if (targetTask.getCompletedAt() == null) {
              targetTask.setCompletedAt(Instant.now());
            }
          }
        }

        taskRepository.save(targetTask);
        return taskMapper.taskToResponse(targetTask);
      })
      .orElseThrow(() -> new TaskNotFoundException(taskId));
  }

  public TaskResponse reopenTask(Long id, UpdateTaskRequest changes) {
    return taskRepository
      .findByIdAndUser_Id(id, currentUserId())
      .map(task -> {
        if (task.getStatus() != TaskStatus.COMPLETED) {
          throw new TaskNotCompletedException(List.of(id));
        }
        taskMapper.updateTaskFromRequest(changes, task);
        // Enforce the reopened state after mapping.
        task.setStatus(TaskStatus.PENDING);
        task.setCompletedAt(null);
        task.setArchived(false);
        task.setArchivedAt(null);

        return taskMapper.taskToResponse(taskRepository.save(task));
      })
      .orElseThrow(() -> new TaskNotFoundException(id));
  }

  @SuppressWarnings("null")
  @Transactional
  public List<TaskResponse> archiveTasks(List<Long> taskIds) {
    taskIds = validatedTaskIds(taskIds);
    List<Task> tasks = taskRepository.findAllByIdInAndUser_Id(
      taskIds,
      currentUserId()
    );

    // ids the client sent that don't exist
    Set<Long> foundIds = tasks
      .stream()
      .map(Task::getId)
      .collect(Collectors.toSet());

    List<Long> missingIds = taskIds
      .stream()
      .filter(id -> !foundIds.contains(id))
      .distinct()
      .toList();

    if (!missingIds.isEmpty()) {
      throw new TaskNotFoundException(missingIds);
    }

    List<Long> incompleteTasksIds = tasks
      .stream()
      .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
      .map(Task::getId)
      .toList();

    // only completed tasks can be archived
    if (!incompleteTasksIds.isEmpty()) {
      throw new TaskNotCompletedException(incompleteTasksIds);
    }

    Instant now = Instant.now();
    tasks.forEach(t -> {
      t.setArchived(true);
      t.setArchivedAt(now);
    });

    // Flush updates so @PreUpdate sets updatedAt before mapping to response DTOs.
    List<Task> savedTasks = taskRepository.saveAllAndFlush(tasks);

    return savedTasks.stream().map(taskMapper::taskToResponse).toList();
  }
}
