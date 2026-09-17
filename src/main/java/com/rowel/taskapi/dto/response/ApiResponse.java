package com.rowel.taskapi.dto.response;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiResponse<T> {
    boolean success;
    String message;
    T data;
    Instant timestamp;
    
    public static <T> ApiResponse<T> success(String message, T data) {
		return ApiResponse.<T>builder()
				.success(true)
				.data(data)
				.message(message)
				.timestamp(Instant.now())
				.build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
		return ApiResponse.<T>builder()
				.success(false)
				.data(null)
				.message(message)
				.timestamp(Instant.now())
				.build();
	}
}