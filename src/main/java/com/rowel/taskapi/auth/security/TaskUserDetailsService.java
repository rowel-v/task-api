package com.rowel.taskapi.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rowel.taskapi.auth.exception.UserNotFoundException;
import com.rowel.taskapi.auth.model.TaskUser;
import com.rowel.taskapi.auth.repository.TaskUserRepository;


@Service
public class TaskUserDetailsService implements UserDetailsService {

  public TaskUserDetailsService(TaskUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  private final TaskUserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    TaskUser user = userRepository
      .findByUsername(username)
      .orElseThrow(() -> UsernameNotFoundException.fromUsername(username));

    return new TaskUserDetails(user);
  }

  public UserDetails loadByUserId(Long userId) {
    TaskUser user = userRepository
      .findById(userId)
      .orElseThrow(UserNotFoundException::new);

    return new TaskUserDetails(user);
  }
}
