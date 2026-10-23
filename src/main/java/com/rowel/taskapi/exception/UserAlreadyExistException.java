package com.rowel.taskapi.exception;

public class UserAlreadyExistException extends RuntimeException {

  public UserAlreadyExistException() {
    super("User already exists.");
  }
}
