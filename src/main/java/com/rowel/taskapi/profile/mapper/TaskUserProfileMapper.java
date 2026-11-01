package com.rowel.taskapi.profile.mapper;

import com.rowel.taskapi.profile.dto.request.SaveTaskUserProfileRequest;
import com.rowel.taskapi.profile.dto.response.TaskUserProfileResponse;
import com.rowel.taskapi.profile.model.TaskUserProfile;
import org.springframework.stereotype.Component;

@Component
public class TaskUserProfileMapper {

  public TaskUserProfileResponse toResponse(TaskUserProfile taskUserProfile) {
    var t = taskUserProfile;
    return new TaskUserProfileResponse(
      t.getId(),
      t.getUser().getUsername(),
      t.getDisplayName(),
      t.getBio(),
      t.getTimeZone(),
      t.getPhotoData() != null
    );
  }

  public void updateProfile(
    SaveTaskUserProfileRequest request,
    TaskUserProfile profile
  ) {
    profile.setDisplayName(request.displayName());
    profile.setBio(request.bio());
    profile.setTimeZone(request.timeZone());
  }
}
