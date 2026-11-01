package com.rowel.taskapi.profile.exception;

public class ProfilePhotoNotFoundException extends RuntimeException {

  public ProfilePhotoNotFoundException() {
    super("Profile photo not found.");
  }
}
