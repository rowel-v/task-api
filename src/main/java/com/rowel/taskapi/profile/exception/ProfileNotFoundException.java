package com.rowel.taskapi.profile.exception;

public class ProfileNotFoundException extends RuntimeException {

  public ProfileNotFoundException() {
    super("Profile not found.");
  }
}
