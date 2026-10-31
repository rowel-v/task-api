package com.rowel.taskapi.task.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import com.rowel.taskapi.auth.model.TaskUser;

@Entity
@Table(name = "task")
@SoftDelete(strategy = SoftDeleteType.TIMESTAMP, columnName = "deleted_at")
public class Task {

  protected Task() {}

  public static TaskBuilder builder() {
    return new TaskBuilder();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private TaskUser user;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(length = 2000)
  private String description;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TaskStatus status;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TaskPriority priority;

  @Column(nullable = false)
  private boolean archived = false;

  private Instant dueDate;

  private Instant createdAt;
  private Instant updatedAt;
  private Instant completedAt;
  private Instant archivedAt;

  @Column(name = "deleted_at", insertable = false, updatable = false)
  private Instant deletedAt;

  @PrePersist
  private void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  @PreUpdate
  private void onUpdate() {
    updatedAt = Instant.now();
  }

  private Task(TaskBuilder taskBuilder) {
    this.id = taskBuilder.id;
    this.user = taskBuilder.user;
    this.name = taskBuilder.name;
    this.description = taskBuilder.description;
    this.status = taskBuilder.status;
    this.priority = taskBuilder.priority;
    this.dueDate = taskBuilder.dueDate;
    this.completedAt = taskBuilder.completedAt;
    this.archived = taskBuilder.archived;
    this.archivedAt = taskBuilder.archivedAt;
  }

  @Override
  public int hashCode() {
    return Task.class.hashCode();
  }

  // two tasks are equal when they have the same id
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Task other)) return false;
    return getId() != null && getId().equals(other.getId());
  }

  public Long getId() {
    return id;
  }

  public TaskUser getUser() {
    return user;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public TaskStatus getStatus() {
    return status;
  }

  public void setStatus(TaskStatus status) {
    this.status = status;
  }

  public TaskPriority getPriority() {
    return priority;
  }

  public void setPriority(TaskPriority priority) {
    this.priority = priority;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Instant getDueDate() {
    return dueDate;
  }

  public void setDueDate(Instant dueDate) {
    this.dueDate = dueDate;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(Instant completedAt) {
    this.completedAt = completedAt;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public Instant getArchivedAt() {
    return archivedAt;
  }

  public void setArchivedAt(Instant archivedAt) {
    this.archivedAt = archivedAt;
  }

  public Instant getDeletedAt() {
    return deletedAt;
  }

  public static class TaskBuilder {

    private Long id;
    private TaskUser user;
    private String name;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Instant dueDate;
    private Instant completedAt;
    private boolean archived;
    private Instant archivedAt;

    public TaskBuilder user(TaskUser user) {
      this.user = user;
      return this;
    }

    public TaskBuilder name(String name) {
      this.name = name;
      return this;
    }

    public TaskBuilder description(String description) {
      this.description = description;
      return this;
    }

    public TaskBuilder status(TaskStatus status) {
      this.status = status;
      return this;
    }

    public TaskBuilder priority(TaskPriority priority) {
      this.priority = priority;
      return this;
    }

    public TaskBuilder dueDate(Instant dueDate) {
      this.dueDate = dueDate;
      return this;
    }

    public TaskBuilder completedAt(Instant completedAt) {
      this.completedAt = completedAt;
      return this;
    }

    public TaskBuilder archived(boolean archived) {
      this.archived = archived;
      return this;
    }

    public TaskBuilder archivedAt(Instant archivedAt) {
      this.archivedAt = archivedAt;
      return this;
    }

    public Task build() {
      return new Task(this);
    }
  }
}
