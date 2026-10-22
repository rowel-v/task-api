package com.rowel.taskapi.service;

import com.rowel.taskapi.dto.request.CreateTaskRequest;
import com.rowel.taskapi.dto.request.UpdateTaskRequest;
import com.rowel.taskapi.dto.response.TaskResponse;
import com.rowel.taskapi.exception.TaskAlreadyCompletedException;
import com.rowel.taskapi.exception.TaskNotCompletedException;
import com.rowel.taskapi.exception.TaskNotFoundException;
import com.rowel.taskapi.mapper.TaskMapper;
import com.rowel.taskapi.model.Task;
import com.rowel.taskapi.model.TaskStatus;
import com.rowel.taskapi.repository.TaskRepository;
import com.rowel.taskapi.shared.TaskStatusAction;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final TaskMapper taskMapper;

  public TaskService(TaskRepository tRepository, TaskMapper tMapper) {
    this.taskRepository = tRepository;
    this.taskMapper = tMapper;
  }

  public List<TaskResponse> getAllTasks() {
    return taskRepository
      .findAll()
      .stream()
      .map(taskMapper::taskToResponse)
      .toList();
  }

  public TaskResponse createTask(CreateTaskRequest req) {
    Task task = taskMapper.requestToTask(req);
    Task saved = taskRepository.save(task);
    return taskMapper.taskToResponse(saved);
  }

  public TaskResponse updateTask(Long id, UpdateTaskRequest req) {
    return taskRepository
      .findById(id)
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

  @SuppressWarnings("null")
  @Transactional
  public void deleteTask(List<Long> taskIds) {
    List<Task> tasks = taskRepository.findAllById(taskIds);

    Set<Long> foundIds = tasks
      .stream()
      .map(Task::getId)
      .collect(Collectors.toSet());

    // ids the client sent that don't exist
    List<Long> missingIds = taskIds
      .stream()
      .filter(id -> !foundIds.contains(id))
      .toList();

    if (!missingIds.isEmpty()) {
      throw new TaskNotFoundException(missingIds);
    }

    taskRepository.deleteAll(tasks);
  }

  public TaskResponse updateTaskStatus(
    Long taskId,
    TaskStatusAction taskStatusAction
  ) {
    return taskRepository 
      .findById(taskId)
      .map(targetTask -> {
        switch (taskStatusAction) {
          case START -> targetTask.setStatus(TaskStatus.IN_PROGRESS);
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
      .findById(id)
      .map(task -> {
        taskMapper.updateTaskFromRequest(changes, task);
        return taskMapper.taskToResponse(taskRepository.save(task));
      })
      .orElseThrow(() -> new TaskNotFoundException(id));
  }

  @SuppressWarnings("null")
  @Transactional
  public List<TaskResponse> archiveTasks(List<Long> taskIds) {
    List<Task> tasks = taskRepository.findAllById(taskIds);

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

    taskRepository.saveAll(tasks);

    return tasks.stream().map(taskMapper::taskToResponse).toList();
  }
}
