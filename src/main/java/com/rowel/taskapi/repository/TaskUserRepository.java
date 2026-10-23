package com.rowel.taskapi.repository;

import com.rowel.taskapi.model.TaskUser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskUserRepository extends JpaRepository<TaskUser, Long> {

    Optional<TaskUser> findByUsername(String username);
}
