package com.wilove.vaulten.exception;

/**
 * Exception thrown when a user tries to access a resource they don't own.
 */
public class AccessDeniedException extends RuntimeException {
    public String getMessage() {
        return "You do not have permission to access this resource";
    }
}
