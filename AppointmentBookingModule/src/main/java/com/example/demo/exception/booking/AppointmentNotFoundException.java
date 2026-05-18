package com.example.demo.exception.booking;

/**
 * Exception thrown when an appointment cannot be found by its ID.
 */
public class AppointmentNotFoundException extends AppointmentException {
    public AppointmentNotFoundException(String id) {
        super("Appointment not found with ID: " + id);
    }
}
