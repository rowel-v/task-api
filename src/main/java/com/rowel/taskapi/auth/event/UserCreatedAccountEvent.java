package com.rowel.taskapi.auth.event;

import org.springframework.modulith.NamedInterface;

import com.rowel.taskapi.auth.model.TaskUser;

@NamedInterface(name = "task_user_created_account")
public record UserCreatedAccountEvent(TaskUser taskUser) {}
