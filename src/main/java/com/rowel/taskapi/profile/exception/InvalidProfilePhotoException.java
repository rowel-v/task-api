package com.rowel.taskapi.profile.exception;

public class InvalidProfilePhotoException extends RuntimeException {

  public InvalidProfilePhotoException() {
    super("Choose a valid JPEG or PNG image, at most 4096 pixels wide and high.");
  }

  public InvalidProfilePhotoException(String message) {
    super(message);
  }
}
