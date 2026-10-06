package com.rowel.taskapi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record DeleteTaskRequest(
  @NotEmpty(message = "{task.ids.notempty}") List<Long> taskIds
) {}
