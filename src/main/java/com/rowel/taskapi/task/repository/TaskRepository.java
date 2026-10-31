package com.rowel.taskapi.task.repository;

import com.rowel.taskapi.auth.model.TaskUser;
import com.rowel.taskapi.task.model.Task;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByUser(TaskUser user);
  Optional<Task> findByIdAndUser_Id(Long id, Long userId);
  List<Task> findAllByIdInAndUser_Id(List<Long> ids, Long userId);
}
