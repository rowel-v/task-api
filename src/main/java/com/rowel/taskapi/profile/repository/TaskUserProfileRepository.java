package com.rowel.taskapi.profile.repository;

import com.rowel.taskapi.profile.model.TaskUserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.modulith.NamedInterface;

@NamedInterface(name = "task_user_profile_repository")
public interface TaskUserProfileRepository extends JpaRepository<TaskUserProfile, Long> {
  Optional<TaskUserProfile> findByUser_Id(Long userId);
}
