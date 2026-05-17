package com.example.demo.repository.booking;

import com.example.demo.model.booking.Appointment;
import com.example.demo.util.booking.AppointmentFileHandler;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

/**
 * AppointmentRepository - Data Access Layer
 * Acts as an abstraction layer over the file handler, keeping data access
 * separate from business logic. Managed by Spring IoC Container.
 */
@Repository
public class AppointmentRepository {

    private final AppointmentFileHandler fileHandler;

    /**
     * Constructor Injection for Spring DI
     */
    public AppointmentRepository(AppointmentFileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    /**
     * Add a new appointment to the storage.
     * @param appointment the appointment to save
     * @return true if successfully saved
     * @throws IOException if a file error occurs or conflict exists
     */
    public boolean addAppointment(Appointment appointment) throws IOException {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }
        return fileHandler.saveAppointment(appointment);
    }

    /**
     * Retrieve all appointments from storage (served from cache).
     * @return List of all appointments
     */
    public List<Appointment> getAllAppointments() {
        return fileHandler.readAllAppointments();
    }

    /**
     * Retrieve a specific appointment by its ID.
     * @param id the appointment ID
     * @return the Appointment object if found, otherwise null
     */
    public Appointment getAppointmentById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return fileHandler.searchAppointmentById(id);
    }

    /**
     * Update an existing appointment in storage.
     * @param appointment the updated appointment object
     * @return true if successfully updated
     * @throws IOException if a file error occurs or conflict exists
     */
    public boolean updateAppointment(Appointment appointment) throws IOException {
        if (appointment == null || appointment.getAppointmentId() == null) {
            throw new IllegalArgumentException("Invalid appointment data for update");
        }
        return fileHandler.updateAppointment(appointment);
    }

    /**
     * Delete an appointment from storage by its ID.
     * @param id the appointment ID to delete
     * @return true if successfully deleted
     * @throws IOException if a file error occurs
     */
    public boolean deleteAppointment(String id) throws IOException {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return fileHandler.deleteAppointment(id);
    }
}
