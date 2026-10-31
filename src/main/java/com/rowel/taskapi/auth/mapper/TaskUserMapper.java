package com.rowel.taskapi.auth.mapper;

import com.rowel.taskapi.auth.dto.request.CreateAccountRequest;
import com.rowel.taskapi.auth.model.TaskUser;
import com.rowel.taskapi.auth.security.TaskUserDetails;
import org.springframework.stereotype.Component;

@Component
public class TaskUserMapper {

  public TaskUser toEntity(CreateAccountRequest req) {
    return TaskUser.builder()
      .username(req.username())
      .password(req.password())
      .build();
  }

  public TaskUser toEntity(TaskUserDetails req) {
    return TaskUser.builder()
      .id(req.getUserId())
      .username(req.getUsername())
      .password(req.getPassword())
      .build();
  }
}
