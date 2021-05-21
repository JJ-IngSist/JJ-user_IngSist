package com.edu.austral.ingsis.exception;

public class NotFoundException extends RuntimeException {

  public NotFoundException() {
    super("Entity not found");
  }
}
