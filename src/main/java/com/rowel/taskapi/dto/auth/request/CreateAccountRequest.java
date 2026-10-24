package com.rowel.taskapi.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
  
  @NotBlank
  @Size(max = 50)
  String username,
  
  @NotBlank
  @Size(max = 255)
  String password
) {}
