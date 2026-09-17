package com.rowel.taskapi.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.rowel.taskapi.dto.request.CreateTaskRequest;
import com.rowel.taskapi.dto.request.UpdateTaskRequest;
import com.rowel.taskapi.dto.response.TaskResponse;
import com.rowel.taskapi.exception.TaskNotFoundException;
import com.rowel.taskapi.mapper.TaskMapper;
import com.rowel.taskapi.model.Task;
import com.rowel.taskapi.model.TaskStatus;
import com.rowel.taskapi.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class TaskService {
	
	private final TaskRepository taskRepository;
	private final TaskMapper taskMapper;
	
	public TaskResponse createTask(CreateTaskRequest req) {
		Task task = taskMapper.requestToTask(req);
		Task saved = taskRepository.save(task);
		return taskMapper.taskToResponse(saved);
	}
	
	public TaskResponse updateTask(Long id, UpdateTaskRequest req) {
	    Task task = taskRepository.findById(id)
	        .orElseThrow(() -> new TaskNotFoundException(id));

	    taskMapper.updateTaskFromRequest(req, task);

	    if (req.getStatus() == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
	        task.setCompletedAt(Instant.now());
	    }

	    return taskMapper.taskToResponse(taskRepository.save(task));
	}

}
