package com.rowel.taskapi.dto.response;

import java.time.Instant;

public record ApiResponse<T>(
  String message,
  T data,
  Instant timestamp
) {
  public static <T> ApiResponse<T> successful(String message, T data) {
    return new ApiResponse<T>(message, data, Instant.now());
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<T>(message, null, Instant.now());
  }
}
