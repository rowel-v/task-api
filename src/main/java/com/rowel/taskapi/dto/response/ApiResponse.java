package com.rowel.taskapi.dto.response;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record ApiResponse<T>(
  @Schema(requiredMode = RequiredMode.REQUIRED)
  String message,
  
  T data,
  
  @Schema(requiredMode = RequiredMode.REQUIRED)
  Instant timestamp
) {
  
  public static <T> ApiResponse<T> successful(String message, T data) {
    return new ApiResponse<T>(message, data, Instant.now());
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<T>(message, null, Instant.now());
  }
}
