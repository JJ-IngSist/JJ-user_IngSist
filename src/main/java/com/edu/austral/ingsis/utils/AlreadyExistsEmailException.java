package com.edu.austral.ingsis.utils;

public class AlreadyExistsEmailException extends RuntimeException {

  public AlreadyExistsEmailException() {
    super("Entity already exists");
  }
}
