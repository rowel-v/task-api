package com.rowel.taskapi.auth.security;

import com.rowel.taskapi.auth.exception.UserNotFoundException;
import com.rowel.taskapi.auth.model.TaskUser;
import com.rowel.taskapi.auth.repository.TaskUserRepository;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@NamedInterface("current_user")
@Service
public class CurrentUserService {

  private final TaskUserRepository userRepository;

  public CurrentUserService(TaskUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Long getId() {
    Authentication authentication =
      SecurityContextHolder.getContext().getAuthentication();

    if (
      authentication == null ||
      !authentication.isAuthenticated() ||
      !(authentication.getPrincipal() instanceof TaskUserDetails user)
    ) {
      throw new AuthenticationCredentialsNotFoundException(
        "Authentication is required"
      );
    }

    return user.getUserId();
  }

  public TaskUser get() {
    return userRepository
      .findById(getId())
      .orElseThrow(UserNotFoundException::new);
  }
}
