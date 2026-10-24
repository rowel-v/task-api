package com.rowel.taskapi.repository;

import com.rowel.taskapi.model.Task;
import com.rowel.taskapi.model.TaskUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByUser(TaskUser user);
  Optional<Task> findByIdAndUser_Id(Long id, Long userId);
  List<Task> findAllByIdInAndUser_Id(List<Long> ids, Long userId);
}
