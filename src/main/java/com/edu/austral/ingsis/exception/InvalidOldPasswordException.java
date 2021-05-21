package com.edu.austral.ingsis.exception;

public class InvalidOldPasswordException extends RuntimeException {
    public InvalidOldPasswordException() {
        super("Old password doesn't match");
    }
}
