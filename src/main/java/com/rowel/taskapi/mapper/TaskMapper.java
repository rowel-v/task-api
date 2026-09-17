package com.rowel.taskapi.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.rowel.taskapi.dto.request.CreateTaskRequest;
import com.rowel.taskapi.dto.request.UpdateTaskRequest;
import com.rowel.taskapi.dto.response.TaskResponse;
import com.rowel.taskapi.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", constant = "PENDING" )
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "completedAt", ignore = true)
	Task requestToTask(CreateTaskRequest createTaskRequest);
	
	TaskResponse taskToResponse(Task task);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "completedAt", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateTaskFromRequest(UpdateTaskRequest req, @MappingTarget Task task);

}
