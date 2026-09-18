package com.rowel.taskapi.dto.response;

import java.time.Instant;

public record ApiResponse<T>(
  boolean success,
  String message,
  T data,
  Instant timestamp
) {
  public static <T> ApiResponse<T> successfull(String message, T data) {
    return new ApiResponse<T>(true, message, data, Instant.now());
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<T>(false, message, null, Instant.now());
  }
}
