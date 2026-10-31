package com.rowel.taskapi.auth.exception;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException() {
    super("User already exists.");
  }

  public UserAlreadyExistsException(Throwable cause) {
    super("User already exists.", cause);
  }
}
