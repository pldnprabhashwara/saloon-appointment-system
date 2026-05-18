package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Regular Appointment - Inheritance
 */
public class RegularAppointment extends Appointment {
    private static final long serialVersionUID = 1L;

    public RegularAppointment() {
        super();
    }

    public RegularAppointment(String customerName, String customerPhone, String serviceType, String stylistName, LocalDate appointmentDate, LocalTime appointmentTime, double price) {
        super(customerName, customerPhone, serviceType, stylistName, appointmentDate, appointmentTime, price);
        calculatePrice(); // Ensure final price is calculated
    }

    @Override
    public void calculatePrice() {
        // Base price remains unchanged for regular
    }

    @Override
    public String displayDetails() {
        return "[REGULAR] " + super.displayDetails();
    }

    @Override
    public String toCSV() {
        return String.format("REGULAR,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,", 
                getAppointmentId(), getCustomerName().replace(",", "\\,"), getCustomerPhone(), 
                getServiceType(), getStylistName(), getAppointmentDate(), getAppointmentTime(), 
                getBookingStatus(), getPrice());
    }
}
