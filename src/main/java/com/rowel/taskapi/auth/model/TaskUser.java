package com.rowel.taskapi.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.springframework.modulith.NamedInterface;

@NamedInterface(name = "model")
@Entity
@Table(name = "`user`")
@SoftDelete(strategy = SoftDeleteType.TIMESTAMP, columnName = "deleted_at")
public class TaskUser {

  public TaskUser() {}

  public static UserBuilder builder() {
    return new UserBuilder();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String password;

  @Column(name = "display_name", length = 100)
  private String displayName;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at", insertable = false, updatable = false)
  private Instant deletedAt;

  @PrePersist
  private void onCreate() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  private void onUpdate() {
    this.updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Instant getDeletedAt() {
    return deletedAt;
  }

  @Override
  public int hashCode() {
    return TaskUser.class.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof TaskUser other)) return false;

    return getId() != null && getId().equals(other.getId());
  }

  private TaskUser(UserBuilder userBuilder) {
    this.id = userBuilder.id;
    this.username = userBuilder.username;
    this.password = userBuilder.password;
    this.displayName = userBuilder.displayName;
  }

  public static class UserBuilder {

    private Long id;
    private String username;
    private String password;
    private String displayName;

    public UserBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public UserBuilder username(String username) {
      this.username = username;
      return this;
    }

    public UserBuilder password(String password) {
      this.password = password;
      return this;
    }

    public UserBuilder displayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public TaskUser build() {
      return new TaskUser(this);
    }
  }
}
