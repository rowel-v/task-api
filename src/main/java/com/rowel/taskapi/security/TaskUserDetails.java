package com.rowel.taskapi.security;

import com.rowel.taskapi.model.TaskUser;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class TaskUserDetails implements UserDetails {

  private final Long userId;
  private final String username;
  private final String password;

  public TaskUserDetails(TaskUser user) {
    this.userId = user.getId();
    this.username = user.getUsername();
    this.password = user.getPassword();
  }

  public Long getUserId() {
    return this.userId;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public @Nullable String getPassword() {
    return this.password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }
}
