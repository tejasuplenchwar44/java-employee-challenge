package com.reliaquest.api.exception;

/**
 * General service exception for employee operations.
 * This follows clean coding practices with specific exception handling.
 */
public class EmployeeServiceException extends RuntimeException {

    public EmployeeServiceException(String message) {
        super(message);
    }

    public EmployeeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
