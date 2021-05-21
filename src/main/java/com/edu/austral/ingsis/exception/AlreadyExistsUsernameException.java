package com.edu.austral.ingsis.exception;

public class AlreadyExistsUsernameException extends RuntimeException {

  public AlreadyExistsUsernameException() {
    super("Entity with this username already exists");
  }
}
