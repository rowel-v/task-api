package com.rowel.taskapi.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder @AllArgsConstructor(access = AccessLevel.PACKAGE)
@Data
@Entity @Table(name = "task")
public class Task {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String description;
	
	@Enumerated(EnumType.STRING)
	private TaskStatus status;
	
	@Enumerated(EnumType.STRING)
	private TaskPriority priority;
	
	private Instant createdAt;
	private Instant updatedAt;
	private Instant dueDate;
	private Instant completedAt;
	
	@PrePersist
	private void onCreate() {
		createdAt = Instant.now();
		updatedAt = Instant.now();
	}
	
	@PreUpdate
	private void onUpdate() {
		updatedAt = Instant.now();
	}

}
