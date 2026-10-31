package com.rowel.taskapi.auth.repository;

import com.rowel.taskapi.auth.model.TaskUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.modulith.NamedInterface;

@NamedInterface(name = "repository")
public interface TaskUserRepository extends JpaRepository<TaskUser, Long> {
  Optional<TaskUser> findByUsername(String username);
}
