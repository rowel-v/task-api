package com.rowel.taskapi.profile.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record TaskUserProfileResponse(

  @Schema(requiredMode = RequiredMode.REQUIRED)
  Long id,

  @Schema(requiredMode = RequiredMode.REQUIRED)
  String username,

  @Schema(nullable = true)
  String displayName,

  @Schema(nullable = true)
  String bio,

  @Schema(nullable = true)
  String timeZone,

  @Schema(requiredMode = RequiredMode.REQUIRED)
  boolean hasPhoto

) {}
