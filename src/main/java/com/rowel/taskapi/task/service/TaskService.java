package com.rowel.taskapi.task.service;

import com.rowel.taskapi.auth.model.TaskUser;
import com.rowel.taskapi.auth.security.CurrentUserService;
import com.rowel.taskapi.profile.model.TaskUserProfile;
import com.rowel.taskapi.profile.repository.TaskUserProfileRepository;
import com.rowel.taskapi.task.dto.request.CreateTaskRequest;
import com.rowel.taskapi.task.dto.request.UpdateTaskRequest;
import com.rowel.taskapi.task.dto.response.TaskResponse;
import com.rowel.taskapi.task.dto.response.UserWithTasksResponse;
import com.rowel.taskapi.task.exception.InvalidTaskIdsException;
import com.rowel.taskapi.task.exception.TaskAlreadyCompletedException;
import com.rowel.taskapi.task.exception.TaskNotCompletedException;
import com.rowel.taskapi.task.exception.TaskNotFoundException;
import com.rowel.taskapi.task.mapper.TaskMapper;
import com.rowel.taskapi.task.model.Task;
import com.rowel.taskapi.task.model.TaskStatus;
import com.rowel.taskapi.task.model.TaskStatusAction;
import com.rowel.taskapi.task.repository.TaskRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

  private final CurrentUserService authenticatedUser;
  private final TaskRepository taskRepository;
  private final TaskUserProfileRepository userProfileRepository;
  private final TaskMapper taskMapper;

  public TaskService(
    CurrentUserService authenticatedUser,
    TaskRepository taskRepository,
    TaskUserProfileRepository userProfileRepository,
    TaskMapper taskMapper
  ) {
    this.authenticatedUser = authenticatedUser;
    this.taskRepository = taskRepository;
    this.userProfileRepository = userProfileRepository;
    this.taskMapper = taskMapper;
  }

  public UserWithTasksResponse getAllTasks() {
    TaskUser user = authenticatedUser.get();

    Optional<TaskUserProfile> profile = userProfileRepository.findByUser_Id(
      user.getId()
    );

    String userDisplayName = profile
      .map(TaskUserProfile::getDisplayName)
      .orElse(null);

    String userTimeZone = profile
      .map(TaskUserProfile::getTimeZone)
      .orElse(null);

    String userBio = profile
      .map(TaskUserProfile::getBio)
      .orElse(null);

    List<Task> tasks = taskRepository.findByUser(user);

    return taskMapper.tasksToResponse(
      user,
      userDisplayName,
      userTimeZone,
      userBio,
      tasks
    );
  }

  public TaskResponse createTask(CreateTaskRequest req) {
    TaskUser user = authenticatedUser.get();
    Task task = taskMapper.requestToTask(req, user);
    Task saved = taskRepository.save(task);
    return taskMapper.taskToResponse(saved);
  }

  public TaskResponse updateTask(Long id, UpdateTaskRequest req) {
    return taskRepository
      .findByIdAndUser_Id(id, authenticatedUser.getId())
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
      authenticatedUser.getId()
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
      .findByIdAndUser_Id(taskId, authenticatedUser.getId())
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
      .findByIdAndUser_Id(id, authenticatedUser.getId())
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
      authenticatedUser.getId()
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
