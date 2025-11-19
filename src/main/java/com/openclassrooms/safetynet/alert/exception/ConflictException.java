package com.openclassrooms.safetynet.alert.exception;

/** Exception thrown when there is a business conflict (e.g. resource already exists). */
public class ConflictException extends RuntimeException {

    public ConflictException() {
        super();
    }

    public ConflictException(String message) {
        super(message);
    }
}
