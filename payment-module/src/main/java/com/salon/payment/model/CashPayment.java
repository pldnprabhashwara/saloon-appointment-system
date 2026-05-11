package com.salon.payment.model;

/**
 * Represents a cash payment.
 * Demonstrates inheritance and polymorphism.
 */
public class CashPayment extends Payment {
    private double amountReceived;
    private double changeGiven;

    public CashPayment() {
        super();
        setPaymentType("CASH");
    }

    public CashPayment(String paymentId, String customerId, String customerName,
                       String appointmentId, double amount, String status,
                       String paymentDate, double amountReceived) {
        super(paymentId, customerId, customerName, appointmentId, amount, status, paymentDate, "CASH");
        this.amountReceived = amountReceived;
        this.changeGiven = amountReceived - amount;
    }

    public CashPayment(String paymentId, String customerId, String customerName,
                       String appointmentId, double amount, String status,
                       String paymentDate, double amountReceived, double changeGiven) {
        super(paymentId, customerId, customerName, appointmentId, amount, status, paymentDate, "CASH");
        this.amountReceived = amountReceived;
        this.changeGiven = changeGiven;
    }

    @Override
    public String processPayment() {
        return String.format(
                "Processing cash payment of Rs. %.2f for %s | Amount Received: Rs. %.2f | Change: Rs. %.2f",
                getAmount(), getCustomerName(), amountReceived, changeGiven
        );
    }

    @Override
    public String getPaymentDetails() {
        return String.format("Cash Payment | Received: Rs. %.2f | Change Given: Rs. %.2f",
                amountReceived, changeGiven);
    }

    public double getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(double amountReceived) {
        this.amountReceived = amountReceived;
        this.changeGiven = amountReceived - getAmount();
    }

    public double getChangeGiven() {
        return changeGiven;
    }

    public void setChangeGiven(double changeGiven) {
        this.changeGiven = changeGiven;
    }
}
