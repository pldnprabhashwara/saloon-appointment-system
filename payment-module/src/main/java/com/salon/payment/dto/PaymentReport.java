package com.salon.payment.dto;

public class PaymentReport {
    private int totalPayments;
    private int completedPayments;
    private int pendingPayments;
    private int failedPayments;
    private int refundedPayments;
    private int cardPayments;
    private int cashPayments;
    private double totalRevenue;
    private double completedRevenue;
    private double pendingAmount;

    public int getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(int totalPayments) {
        this.totalPayments = totalPayments;
    }

    public int getCompletedPayments() {
        return completedPayments;
    }

    public void setCompletedPayments(int completedPayments) {
        this.completedPayments = completedPayments;
    }

    public int getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(int pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

    public int getFailedPayments() {
        return failedPayments;
    }

    public void setFailedPayments(int failedPayments) {
        this.failedPayments = failedPayments;
    }

    public int getRefundedPayments() {
        return refundedPayments;
    }

    public void setRefundedPayments(int refundedPayments) {
        this.refundedPayments = refundedPayments;
    }

    public int getCardPayments() {
        return cardPayments;
    }

    public void setCardPayments(int cardPayments) {
        this.cardPayments = cardPayments;
    }

    public int getCashPayments() {
        return cashPayments;
    }

    public void setCashPayments(int cashPayments) {
        this.cashPayments = cashPayments;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getCompletedRevenue() {
        return completedRevenue;
    }

    public void setCompletedRevenue(double completedRevenue) {
        this.completedRevenue = completedRevenue;
    }

    public double getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(double pendingAmount) {
        this.pendingAmount = pendingAmount;
    }
}
