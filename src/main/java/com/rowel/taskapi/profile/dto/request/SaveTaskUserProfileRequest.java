package com.rowel.taskapi.profile.dto.request;

import jakarta.validation.constraints.Size;

public record SaveTaskUserProfileRequest(

  @Size(max = 100)
  String displayName,

  @Size(max = 500)
  String bio,

  @Size(max = 100)
  String timeZone

) {}
