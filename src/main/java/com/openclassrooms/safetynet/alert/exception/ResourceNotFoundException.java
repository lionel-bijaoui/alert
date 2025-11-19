package com.openclassrooms.safetynet.alert.exception;

/** Exception thrown when a resource does not exist. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
