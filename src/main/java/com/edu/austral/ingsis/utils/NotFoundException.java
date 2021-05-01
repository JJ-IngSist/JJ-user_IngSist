package com.edu.austral.ingsis.utils;

public class NotFoundException extends RuntimeException {

  public NotFoundException() {
    super("Entity not found");
  }
}
