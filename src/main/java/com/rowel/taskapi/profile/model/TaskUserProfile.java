package com.rowel.taskapi.profile.model;

import com.rowel.taskapi.auth.model.TaskUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_profile")
public class TaskUserProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private TaskUser user;

  @Column(name = "display_name", length = 100)
  private String displayName;

  @Lob
  @Column(name = "photo_data", columnDefinition = "varbinary(max)")
  private byte[] photoData;

  @Column(name = "photo_content_type", length = 20)
  private String photoContentType;

  @Column(length = 500)
  private String bio;

  @Column(name = "time_zone", length = 100)
  private String timeZone;

  protected TaskUserProfile() {}

  public static ProfileBuilder builder() {
    return new ProfileBuilder();
  }

  private TaskUserProfile(ProfileBuilder builder) {
    this.id = builder.id;
    this.user = builder.user;
    this.displayName = builder.displayName;
    this.photoData = builder.photoData;
    this.photoContentType = builder.photoContentType;
    this.bio = builder.bio;
    this.timeZone = builder.timeZone;
  }

  public Long getId() {
    return id;
  }

  public TaskUser getUser() {
    return user;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public byte[] getPhotoData() {
    return photoData;
  }

  public void setPhotoData(byte[] photoData) {
    this.photoData = photoData;
  }

  public String getPhotoContentType() {
    return photoContentType;
  }

  public void setPhotoContentType(String photoContentType) {
    this.photoContentType = photoContentType;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public static class ProfileBuilder {

    private Long id;
    private TaskUser user;
    private String displayName;
    private byte[] photoData;
    private String photoContentType;
    private String bio;
    private String timeZone;

    public ProfileBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public ProfileBuilder user(TaskUser user) {
      this.user = user;
      return this;
    }

    public ProfileBuilder displayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public ProfileBuilder photoData(byte[] photoData) {
      this.photoData = photoData;
      return this;
    }

    public ProfileBuilder photoContentType(String photoContentType) {
      this.photoContentType = photoContentType;
      return this;
    }

    public ProfileBuilder bio(String bio) {
      this.bio = bio;
      return this;
    }

    public ProfileBuilder timeZone(String timeZone) {
      this.timeZone = timeZone;
      return this;
    }

    public TaskUserProfile build() {
      return new TaskUserProfile(this);
    }
  }
}
