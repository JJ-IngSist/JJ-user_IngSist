package com.edu.austral.ingsis.exception;

public class AlreadyExistsEmailException extends RuntimeException {

  public AlreadyExistsEmailException() {
    super("Entity already exists");
  }
}
