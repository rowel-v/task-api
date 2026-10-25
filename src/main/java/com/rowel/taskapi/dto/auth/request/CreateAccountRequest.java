package com.rowel.taskapi.dto.auth.request;

import com.rowel.taskapi.validation.Utf8ByteLength;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
  
  @NotBlank
  @Size(max = 50)
  String username,
  
  @NotBlank
  @Size(max = 255)
  @Utf8ByteLength(max = 72)
  String password
) {}
