package com.rowel.taskapi.auth;

import com.rowel.taskapi.auth.model.TaskUser;

public interface UserProfileCreator {
  void createProfile(TaskUser user);
}
