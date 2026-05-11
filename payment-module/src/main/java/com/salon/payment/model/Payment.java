package com.salon.payment.model;

/**
 * Abstract base class for all payment types.
 * Demonstrates abstraction, encapsulation, and inheritance.
 */
public abstract class Payment {
    private String paymentId;
    private String customerId;
    private String customerName;
    private String appointmentId;
    private double amount;
    private String status;
    private String paymentDate;
    private String paymentType;

    public Payment() {
    }

    public Payment(String paymentId, String customerId, String customerName,
                   String appointmentId, double amount, String status,
                   String paymentDate, String paymentType) {
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
    }

    public abstract String processPayment();

    public abstract String getPaymentDetails();

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Customer: %s | Rs. %.2f | %s | %s",
                paymentType, paymentId, customerName, amount, status, paymentDate);
    }
}
