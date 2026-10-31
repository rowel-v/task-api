package com.rowel.taskapi.auth.exception;

import org.springframework.modulith.NamedInterface;

@NamedInterface(name = "user_not_found_exception")
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException() {
    super("User not found");
  }
}
