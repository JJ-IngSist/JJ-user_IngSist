package com.edu.austral.ingsis.utils;

public class AlreadyExistsException extends RuntimeException {

  public AlreadyExistsException() {
    super("Entity already exists");
  }
}
