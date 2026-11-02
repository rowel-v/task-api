package com.rowel.taskapi.profile.listener;

import com.rowel.taskapi.auth.event.UserCreatedAccountEvent;
import com.rowel.taskapi.profile.model.TaskUserProfile;
import com.rowel.taskapi.profile.repository.TaskUserProfileRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TaskUserProfileListener {

  private final TaskUserProfileRepository userProfileRepository;

  public TaskUserProfileListener(
    TaskUserProfileRepository userProfileRepository
  ) {
    this.userProfileRepository = userProfileRepository;
  }

  @EventListener
  public void onUserCreatedAccount(UserCreatedAccountEvent event) {
    TaskUserProfile newUser = TaskUserProfile.builder()
      .user(event.taskUser())
      .build();
    userProfileRepository.save(newUser);
  }
}
