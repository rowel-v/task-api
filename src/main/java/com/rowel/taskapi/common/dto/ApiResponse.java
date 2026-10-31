package com.rowel.taskapi.common.dto;

import java.time.Instant;

import org.springframework.modulith.NamedInterface;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@NamedInterface(name = "api_response")
public record ApiResponse<T>(
  @Schema(requiredMode = RequiredMode.REQUIRED)
  String message,

  T data,

  @Schema(requiredMode = RequiredMode.REQUIRED)
  Instant timestamp
) {

  public static <T> ApiResponse<T> successful(String message, T data) {
    return new ApiResponse<>(message, data, Instant.now());
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(message, null, Instant.now());
  }
}
