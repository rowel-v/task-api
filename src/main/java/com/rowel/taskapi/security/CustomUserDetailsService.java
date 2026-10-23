package com.rowel.taskapi.security;

import com.rowel.taskapi.model.TaskUser;
import com.rowel.taskapi.repository.TaskUserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  public CustomUserDetailsService(TaskUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  private final TaskUserRepository userRepository;

  @Override
  public TaskUserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {

    TaskUser user = userRepository
      .findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found."));

    return new TaskUserDetails(user);
  }
}
