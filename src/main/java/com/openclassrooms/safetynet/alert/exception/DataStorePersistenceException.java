package com.openclassrooms.safetynet.alert.exception;

/** Exception thrown when data persistence operations fail. */
public class DataStorePersistenceException extends RuntimeException {

    public DataStorePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
