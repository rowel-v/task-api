package com.rowel.taskapi.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record DeleteTaskRequest(@NotEmpty  List<Long> taskIds) {
}
