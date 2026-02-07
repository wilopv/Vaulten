package com.wilove.vaulten.exception;

/**
 * Exception thrown when attempting to register a user with duplicate username
 * or email
 */
public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String message) {
        super(message);
    }
}
