package com.salon.payment.model;

/**
 * Represents a credit or debit card payment.
 * Demonstrates inheritance and polymorphism.
 */
public class CardPayment extends Payment {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cardType;

    public CardPayment() {
        super();
        setPaymentType("CARD");
    }

    public CardPayment(String paymentId, String customerId, String customerName,
                       String appointmentId, double amount, String status,
                       String paymentDate, String cardNumber, String cardHolderName,
                       String expiryDate, String cardType) {
        super(paymentId, customerId, customerName, appointmentId, amount, status, paymentDate, "CARD");
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
    }

    @Override
    public String processPayment() {
        return String.format(
                "Processing %s card payment of Rs. %.2f for %s | Card: %s | Holder: %s | Expiry: %s",
                cardType, getAmount(), getCustomerName(), maskCardNumber(cardNumber), cardHolderName, expiryDate
        );
    }

    @Override
    public String getPaymentDetails() {
        return String.format("Card Payment | %s | %s | Holder: %s | Expiry: %s",
                cardType, maskCardNumber(cardNumber), cardHolderName, expiryDate);
    }

    private String maskCardNumber(String number) {
        if (number == null || number.length() < 4) {
            return "****";
        }
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
