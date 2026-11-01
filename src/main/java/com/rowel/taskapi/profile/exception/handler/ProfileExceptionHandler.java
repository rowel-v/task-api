package com.rowel.taskapi.profile.exception.handler;

import com.rowel.taskapi.common.dto.ApiResponse;
import com.rowel.taskapi.profile.exception.InvalidProfilePhotoException;
import com.rowel.taskapi.profile.exception.ProfileNotFoundException;
import com.rowel.taskapi.profile.exception.ProfilePhotoNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProfileExceptionHandler {

  @ExceptionHandler(InvalidProfilePhotoException.class)
  public ResponseEntity<ApiResponse<Void>> handleInvalidPhoto(
    InvalidProfilePhotoException ex
  ) {
    return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler({ProfileNotFoundException.class, ProfilePhotoNotFoundException.class})
  public ResponseEntity<?> handleNotFound(RuntimeException ex, HttpServletRequest request) {
    // An image request may not accept a JSON error body.
    if ("GET".equals(request.getMethod()) && request.getRequestURI().endsWith("/api/profile/photo")) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
  }
}
