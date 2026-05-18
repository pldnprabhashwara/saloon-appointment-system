package com.example.demo.model.booking;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * VIP Appointment - Inheritance and Polymorphism
 */
public class VIPAppointment extends Appointment {
    private static final long serialVersionUID = 1L;
    
    private String complimentaryDrink;

    public VIPAppointment() {
        super();
    }

    public VIPAppointment(String customerName, String customerPhone, String serviceType, String stylistName, LocalDate appointmentDate, LocalTime appointmentTime, double price, String complimentaryDrink) {
        super(customerName, customerPhone, serviceType, stylistName, appointmentDate, appointmentTime, price);
        this.complimentaryDrink = complimentaryDrink;
        calculatePrice(); // Apply VIP pricing logic
    }

    public String getComplimentaryDrink() { return complimentaryDrink; }
    public void setComplimentaryDrink(String complimentaryDrink) { this.complimentaryDrink = complimentaryDrink; }

    @Override
    public void calculatePrice() {
        // VIPs get a 20% markup on the base price
        setPrice(getPrice() * 1.20);
    }

    @Override
    public String displayDetails() {
        return "[VIP] " + super.displayDetails() + " | Perk: " + complimentaryDrink;
    }

    @Override
    public String toCSV() {
        return String.format("VIP,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%s", 
                getAppointmentId(), getCustomerName().replace(",", "\\,"), getCustomerPhone(), 
                getServiceType(), getStylistName(), getAppointmentDate(), getAppointmentTime(), 
                getBookingStatus(), getPrice(), complimentaryDrink);
    }
}
