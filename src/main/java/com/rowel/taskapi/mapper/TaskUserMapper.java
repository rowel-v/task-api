package com.rowel.taskapi.mapper;

import com.rowel.taskapi.dto.request.CreateAccountRequest;
import com.rowel.taskapi.model.TaskUser;
import com.rowel.taskapi.security.TaskUserDetails;
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
      .username(req.getUsername())
      .password(req.getPassword())
      .build();
  }
}
