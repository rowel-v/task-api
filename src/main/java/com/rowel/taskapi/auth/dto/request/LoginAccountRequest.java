package com.rowel.taskapi.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginAccountRequest(

  @NotBlank
  @Size(max = 50)
  String username,

  @NotBlank
  @Size(max = 72)
  String password

) {}
