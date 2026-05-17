package com.example.demo.service.booking;

import com.example.demo.exception.booking.AppointmentException;
import com.example.demo.exception.booking.AppointmentNotFoundException;
import com.example.demo.model.booking.Appointment;
import com.example.demo.model.booking.RegularAppointment;
import com.example.demo.model.booking.VIPAppointment;
import com.example.demo.repository.booking.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AppointmentService - Business Logic Layer
 * Orchestrates operations between Controller and Repository.
 * Validates business rules, handles pricing logic, and utilizes OOP polymorphism.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    /**
     * Constructor Injection for Dependency Injection (IoC)
     */
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Orchestrates the creation of a new appointment, applying validation and business rules.
     */
    public Appointment createAppointment(String type, String customerName, String customerPhone, String serviceType, String stylistName, LocalDate date, LocalTime time, String vipPerk) throws Exception {
        if (customerName == null || customerName.trim().isEmpty() || 
            customerPhone == null || customerPhone.trim().isEmpty() ||
            serviceType == null || serviceType.trim().isEmpty() ||
            stylistName == null || stylistName.trim().isEmpty()) {
            throw new AppointmentException("All fields are required. Please complete the form.");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new AppointmentException("Cannot book an appointment in the past.");
        }

        if (isSlotBooked(stylistName, date, time)) {
             throw new AppointmentException("Time slot conflict: Stylist is already booked for this date and time.");
        }

        double basePrice = calculateServicePrice(serviceType);
        String initialStatus = "Pending";

        Appointment newAppointment;
        if ("VIP".equalsIgnoreCase(type)) {
            newAppointment = new VIPAppointment(customerName, customerPhone, serviceType, stylistName, date, time, basePrice, vipPerk != null ? vipPerk : "Water");
            newAppointment.setBookingStatus(initialStatus);
        } else {
            newAppointment = new RegularAppointment(customerName, customerPhone, serviceType, stylistName, date, time, basePrice);
            newAppointment.setBookingStatus(initialStatus);
        }

        if (appointmentRepository.addAppointment(newAppointment)) {
            return newAppointment;
        }
        return null;
    }

    /**
     * Retrieves all scheduled appointments.
     */
    public List<Appointment> getAppointments() {
        return appointmentRepository.getAllAppointments();
    }

    /**
     * Retrieves an appointment by ID.
     */
    public Appointment getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.getAppointmentById(id);
        if (appointment == null) {
            throw new AppointmentNotFoundException(id);
        }
        return appointment;
    }

    /**
     * Processes an update request for an existing appointment, recalculating prices.
     */
    public boolean updateAppointment(String id, String serviceType, String stylistName, String dateStr, String timeStr, String newStatus) throws Exception {
        Appointment appointment = getAppointmentById(id);
        
        LocalDate date = LocalDate.parse(dateStr);
        LocalTime time = LocalTime.parse(timeStr);
        
        if (date.isBefore(LocalDate.now()) && !appointment.getAppointmentDate().equals(date)) {
            throw new AppointmentException("Cannot reschedule to a past date.");
        }

        if (!newStatus.equals("Pending") && !newStatus.equals("Confirmed") && 
            !newStatus.equals("Completed") && !newStatus.equals("Cancelled")) {
            throw new AppointmentException("Invalid booking status.");
        }

        appointment.setServiceType(serviceType);
        appointment.setStylistName(stylistName);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setBookingStatus(newStatus);
        
        double basePrice = calculateServicePrice(serviceType);
        appointment.setPrice(basePrice);
        appointment.calculatePrice(); // Ensure VIP markup logic is reapplied if applicable
        
        return appointmentRepository.updateAppointment(appointment);
    }

    /**
     * Completely deletes an appointment from the system.
     */
    public boolean deleteAppointment(String id) throws IOException {
        return appointmentRepository.deleteAppointment(id);
    }

    /**
     * Searches appointments by ID, customer name, or phone number.
     */
    public List<Appointment> searchAppointment(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAppointments();
        }
        String lowerQuery = query.toLowerCase();
        return appointmentRepository.getAllAppointments().stream()
                .filter(a -> a.getAppointmentId().toLowerCase().contains(lowerQuery) ||
                             a.getCustomerName().toLowerCase().contains(lowerQuery) ||
                             a.getCustomerPhone().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    /**
     * Centralized pricing logic.
     */
    private double calculateServicePrice(String serviceType) {
        switch (serviceType) {
            case "Haircut": return 30.0;
            case "Beard Trim": return 20.0;
            case "Facial": return 50.0;
            case "Hair Coloring": return 80.0;
            case "Hair Wash": return 15.0;
            default: return 50.0;
        }
    }

    public List<LocalTime> getBookedTimes(LocalDate date, String stylist) {
        return appointmentRepository.getAllAppointments().stream()
                .filter(a -> !"CANCELLED".equalsIgnoreCase(a.getBookingStatus()))
                .filter(a -> a.getAppointmentDate().equals(date))
                .filter(a -> a.getStylistName().equalsIgnoreCase(stylist))
                .map(Appointment::getAppointmentTime)
                .toList();
    }

    /**
     * Checks if a stylist is already booked at a specific date and time.
     */
    private boolean isSlotBooked(String stylistName, LocalDate date, LocalTime time) {
        return appointmentRepository.getAllAppointments().stream()
                .filter(a -> !"Cancelled".equalsIgnoreCase(a.getBookingStatus()))
                .anyMatch(a -> a.getStylistName().equalsIgnoreCase(stylistName) &&
                               a.getAppointmentDate().equals(date) &&
                               a.getAppointmentTime().equals(time));
    }
}
