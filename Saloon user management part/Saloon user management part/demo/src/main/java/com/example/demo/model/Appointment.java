package com.example.demo.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Appointment Model Class - Encapsulation of Appointment Data
 * Demonstrates OOP Concepts: Encapsulation, Abstraction
 */
public abstract class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String appointmentId;
    private String customerName;
    private String customerPhone;
    private String serviceType;
    private String stylistName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String bookingStatus;
    private double price;

    public Appointment() {
        this.appointmentId = UUID.randomUUID().toString();
        this.bookingStatus = "Pending";
    }

    public Appointment(String customerName, String customerPhone, String serviceType, String stylistName, LocalDate appointmentDate, LocalTime appointmentTime, double price) {
        this();
        setCustomerName(customerName);
        setCustomerPhone(customerPhone);
        setServiceType(serviceType);
        setStylistName(stylistName);
        setAppointmentDate(appointmentDate);
        setAppointmentTime(appointmentTime);
        this.price = price;
    }

    // Encapsulation - Getters and Setters with Validation

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        this.customerName = customerName;
    }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) {
        if (customerPhone == null || !customerPhone.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be 10 digits");
        }
        this.customerPhone = customerPhone;
    }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) {
        if (serviceType == null || serviceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Service type cannot be empty");
        }
        this.serviceType = serviceType;
    }

    public String getStylistName() { return stylistName; }
    public void setStylistName(String stylistName) {
        if (stylistName == null || stylistName.trim().isEmpty()) {
            throw new IllegalArgumentException("Stylist name cannot be empty");
        }
        this.stylistName = stylistName;
    }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) {
        if (appointmentDate == null || appointmentDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) {
        if (appointmentTime == null) {
            throw new IllegalArgumentException("Appointment time cannot be empty");
        }
        this.appointmentTime = appointmentTime;
    }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // Business Logic Methods

    public abstract void calculatePrice();

    public String displayDetails() {
        return String.format("Appointment ID: %s | Customer: %s (%s) | Service: %s by %s | Date: %s %s | Status: %s | Price: $%.2f",
                appointmentId, customerName, customerPhone, serviceType, stylistName, appointmentDate, appointmentTime, bookingStatus, price);
    }
    
    public abstract String toCSV();

    @Override
    public String toString() {
        return displayDetails();
    }
}
