package com.rowel.taskapi.service;

import com.rowel.taskapi.dto.request.CreateTaskRequest;
import com.rowel.taskapi.dto.request.DeleteTaskRequest;
import com.rowel.taskapi.dto.request.UpdateTaskRequest;
import com.rowel.taskapi.dto.response.TaskResponse;
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
    Task task = taskRepository
      .findById(id)
      .orElseThrow(() -> new TaskNotFoundException(id));

    taskMapper.updateTaskFromRequest(req, task);

    if (req.status() == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
      task.setCompletedAt(Instant.now());
    }

    return taskMapper.taskToResponse(taskRepository.save(task));
  }

  @SuppressWarnings("null")
  @Transactional
  public void deleteTask(DeleteTaskRequest req) {
    List<Task> tasks = taskRepository.findAllById(req.taskIds());

    Set<Long> foundIds = tasks
      .stream()
      .map(Task::getId)
      .collect(Collectors.toSet());

    // ids the client sent that don't exist
    List<Long> missingIds = req
      .taskIds()
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
          case COMPLETE -> targetTask.setStatus(TaskStatus.COMPLETED);
        }

        taskRepository.save(targetTask);
        return taskMapper.taskToResponse(targetTask);
      })
      .orElseThrow(() -> new TaskNotFoundException(taskId));
  }
}
