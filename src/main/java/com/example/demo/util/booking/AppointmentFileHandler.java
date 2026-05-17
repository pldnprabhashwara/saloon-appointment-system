package com.example.demo.util.booking;

import com.example.demo.model.booking.Appointment;
import com.example.demo.model.booking.RegularAppointment;
import com.example.demo.model.booking.VIPAppointment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Appointment File Handler - Complete File Handling System
 * Manages all I/O operations for the Appointment domain.
 * Optimized with an in-memory cache to reduce disk reads.
 */
@Component
public class AppointmentFileHandler {

    private static final String FILE_PATH = "data/appointments.txt";
    private static final String HEADER = "type,appointmentId,customerName,customerPhone,serviceType,stylistName,appointmentDate,appointmentTime,bookingStatus,price,vipPerk";

    // In-memory cache to optimize read operations
    private final List<Appointment> appointmentCache = new ArrayList<>();

    public AppointmentFileHandler() {
        createFileIfMissing();
    }

    /**
     * Initializes the in-memory cache on Spring Bean creation.
     */
    @PostConstruct
    private void initCache() {
        try {
            appointmentCache.clear();
            appointmentCache.addAll(loadAppointmentsFromFile());
        } catch (IOException e) {
            System.err.println("Failed to initialize appointment cache: " + e.getMessage());
        }
    }

    private void createFileIfMissing() {
        File file = new File(FILE_PATH);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(HEADER);
                writer.newLine();
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
    }

    /**
     * Saves a new appointment to the cache and the file.
     */
    public synchronized boolean saveAppointment(Appointment appointment) throws IOException {
        if (searchAppointmentById(appointment.getAppointmentId()) != null) {
            throw new IllegalArgumentException("Appointment ID already exists.");
        }
        
        if (hasTimeSlotConflict(appointment.getStylistName(), appointment.getAppointmentDate(), appointment.getAppointmentTime())) {
            throw new IllegalArgumentException("Time slot conflict: Stylist is already booked for this date and time.");
        }

        // Add to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(appointment.toCSV());
            writer.newLine();
        }
        
        // Add to cache
        appointmentCache.add(appointment);
        return true;
    }

    /**
     * Retrieves all appointments directly from the in-memory cache.
     */
    public List<Appointment> readAllAppointments() {
        // Return a copy to prevent external modification of the cache list structure
        return new ArrayList<>(appointmentCache);
    }

    /**
     * Reads the file directly from disk (used strictly for cache initialization).
     */
    private List<Appointment> loadAppointmentsFromFile() throws IOException {
        List<Appointment> appointments = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return appointments;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    appointments.add(parseAppointment(line));
                } catch (Exception e) {
                    System.err.println("Skipping invalid record: " + line);
                }
            }
        }
        return appointments;
    }

    public Appointment searchAppointmentById(String id) {
        for (Appointment appt : appointmentCache) {
            if (appt.getAppointmentId().equals(id)) {
                return appt;
            }
        }
        return null;
    }

    /**
     * Updates an appointment in the cache and rewrites the file.
     */
    public synchronized boolean updateAppointment(Appointment updatedAppointment) throws IOException {
        boolean found = false;

        for (int i = 0; i < appointmentCache.size(); i++) {
            if (appointmentCache.get(i).getAppointmentId().equals(updatedAppointment.getAppointmentId())) {
                
                Appointment oldAppt = appointmentCache.get(i);
                boolean timeChanged = !oldAppt.getAppointmentDate().equals(updatedAppointment.getAppointmentDate()) || 
                                      !oldAppt.getAppointmentTime().equals(updatedAppointment.getAppointmentTime()) ||
                                      !oldAppt.getStylistName().equals(updatedAppointment.getStylistName());
                
                if (timeChanged && hasTimeSlotConflict(updatedAppointment.getStylistName(), updatedAppointment.getAppointmentDate(), updatedAppointment.getAppointmentTime())) {
                    throw new IllegalArgumentException("Time slot conflict: Stylist is already booked for this date and time.");
                }

                appointmentCache.set(i, updatedAppointment);
                found = true;
                break;
            }
        }

        if (found) {
            rewriteFile(appointmentCache);
            return true;
        }
        return false;
    }

    /**
     * Deletes an appointment from the cache and rewrites the file.
     */
    public synchronized boolean deleteAppointment(String id) throws IOException {
        boolean removed = appointmentCache.removeIf(appt -> appt.getAppointmentId().equals(id));

        if (removed) {
            rewriteFile(appointmentCache);
            return true;
        }
        return false;
    }

    private void rewriteFile(List<Appointment> appointments) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            writer.write(HEADER);
            writer.newLine();
            for (Appointment appt : appointments) {
                writer.write(appt.toCSV());
                writer.newLine();
            }
        }
    }

    private boolean hasTimeSlotConflict(String stylistName, LocalDate date, LocalTime time) {
        for (Appointment appt : appointmentCache) {
            if ("CANCELLED".equalsIgnoreCase(appt.getBookingStatus())) continue;
            
            if (appt.getStylistName().equalsIgnoreCase(stylistName) && 
                appt.getAppointmentDate().equals(date) && 
                appt.getAppointmentTime().equals(time)) {
                return true;
            }
        }
        return false;
    }

    public static Appointment parseAppointment(String csvLine) {
        String[] parts = csvLine.split(",(?=(?:[^\\\\]|\\\\.)*$)"); 
        
        if (parts.length < 10) {
            throw new IllegalArgumentException("Invalid CSV format for Appointment");
        }

        String type = parts[0];
        String id = parts[1];
        String customerName = parts[2].replace("\\,", ",");
        String customerPhone = parts[3];
        String serviceType = parts[4];
        String stylistName = parts[5];
        LocalDate date = LocalDate.parse(parts[6]);
        LocalTime time = LocalTime.parse(parts[7]);
        String status = parts[8];
        double price = Double.parseDouble(parts[9]);

        Appointment appointment;
        if ("VIP".equalsIgnoreCase(type)) {
            String complimentaryDrink = parts.length > 10 ? parts[10] : "None";
            VIPAppointment vip = new VIPAppointment(customerName, customerPhone, serviceType, stylistName, date, time, price, complimentaryDrink);
            vip.setAppointmentId(id);
            vip.setPrice(price); 
            vip.setBookingStatus(status);
            appointment = vip;
        } else {
            RegularAppointment reg = new RegularAppointment(customerName, customerPhone, serviceType, stylistName, date, time, price);
            reg.setAppointmentId(id);
            reg.setPrice(price);
            reg.setBookingStatus(status);
            appointment = reg;
        }

        return appointment;
    }
}
