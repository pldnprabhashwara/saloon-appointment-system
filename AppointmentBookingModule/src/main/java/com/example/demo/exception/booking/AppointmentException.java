package com.example.demo.exception.booking;

/**
 * Base custom exception for the Appointment module.
 * Extends RuntimeException to provide unchecked exception handling 
 * specific to business logic failures in appointments.
 */
public class AppointmentException extends RuntimeException {
    public AppointmentException(String message) {
        super(message);
    }

    public AppointmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
