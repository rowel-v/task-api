package com.rowel.taskapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rowel.taskapi.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
	
	

}
