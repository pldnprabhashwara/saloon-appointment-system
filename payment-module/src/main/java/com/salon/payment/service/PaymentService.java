package com.salon.payment.service;

import com.salon.payment.dto.PaymentReport;
import com.salon.payment.dto.PaymentRequest;
import com.salon.payment.model.CardPayment;
import com.salon.payment.model.CashPayment;
import com.salon.payment.model.Payment;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String[] VALID_STATUSES = {"PENDING", "COMPLETED", "FAILED", "REFUNDED"};
    private final Path paymentFile;

    public PaymentService(@Value("${payment.file:payments.txt}") String paymentFile) {
        this.paymentFile = Paths.get(paymentFile);
    }

    @PostConstruct
    public void ensurePaymentFileExists() throws IOException {
        Path parent = paymentFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (!Files.exists(paymentFile)) {
            Files.createFile(paymentFile);
        }
    }

    public synchronized Payment makePayment(PaymentRequest request) {
        validateBasePayment(request);

        String type = normalizeType(request.getPaymentType());
        String status = normalizeStatus(defaultIfBlank(request.getStatus(), "COMPLETED"));
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        String paymentDate = LocalDateTime.now().format(DATE_FORMAT);

        Payment payment;
        if ("CARD".equals(type)) {
            validateCardPayment(request);
            payment = new CardPayment(
                    paymentId,
                    clean(request.getCustomerId()),
                    clean(request.getCustomerName()),
                    clean(request.getAppointmentId()),
                    request.getAmount(),
                    status,
                    paymentDate,
                    clean(request.getCardNumber()),
                    clean(request.getCardHolderName()),
                    clean(request.getExpiryDate()),
                    clean(defaultIfBlank(request.getCardType(), "CARD"))
            );
        } else {
            validateCashPayment(request);
            payment = new CashPayment(
                    paymentId,
                    clean(request.getCustomerId()),
                    clean(request.getCustomerName()),
                    clean(request.getAppointmentId()),
                    request.getAmount(),
                    status,
                    paymentDate,
                    request.getAmountReceived()
            );
        }

        List<Payment> payments = loadPayments();
        payments.add(payment);
        writePayments(payments);
        return payment;
    }

    public synchronized List<Payment> getAllPayments() {
        return loadPayments();
    }

    public synchronized Optional<Payment> findPayment(String paymentId) {
        return loadPayments().stream()
                .filter(payment -> payment.getPaymentId().equalsIgnoreCase(paymentId))
                .findFirst();
    }

    public synchronized Payment updatePaymentStatus(String paymentId, String status) {
        String normalizedStatus = normalizeStatus(status);
        List<Payment> payments = loadPayments();

        for (Payment payment : payments) {
            if (payment.getPaymentId().equalsIgnoreCase(paymentId)) {
                payment.setStatus(normalizedStatus);
                writePayments(payments);
                return payment;
            }
        }

        throw new IllegalArgumentException("Payment record not found: " + paymentId);
    }

    public synchronized void deletePayment(String paymentId) {
        List<Payment> payments = loadPayments();
        boolean removed = payments.removeIf(payment -> payment.getPaymentId().equalsIgnoreCase(paymentId));
        if (!removed) {
            throw new IllegalArgumentException("Payment record not found: " + paymentId);
        }
        writePayments(payments);
    }

    public synchronized PaymentReport buildReport() {
        List<Payment> payments = loadPayments();
        PaymentReport report = new PaymentReport();
        report.setTotalPayments(payments.size());

        for (Payment payment : payments) {
            String status = normalizeStatus(payment.getStatus());
            String type = normalizeType(payment.getPaymentType());
            double amount = payment.getAmount();

            report.setTotalRevenue(report.getTotalRevenue() + amount);

            if ("CARD".equals(type)) {
                report.setCardPayments(report.getCardPayments() + 1);
            }
            if ("CASH".equals(type)) {
                report.setCashPayments(report.getCashPayments() + 1);
            }

            switch (status) {
                case "COMPLETED" -> {
                    report.setCompletedPayments(report.getCompletedPayments() + 1);
                    report.setCompletedRevenue(report.getCompletedRevenue() + amount);
                }
                case "PENDING" -> {
                    report.setPendingPayments(report.getPendingPayments() + 1);
                    report.setPendingAmount(report.getPendingAmount() + amount);
                }
                case "FAILED" -> report.setFailedPayments(report.getFailedPayments() + 1);
                case "REFUNDED" -> report.setRefundedPayments(report.getRefundedPayments() + 1);
                default -> {
                }
            }
        }

        return report;
    }

    private List<Payment> loadPayments() {
        try {
            ensurePaymentFileExists();
            List<String> lines = Files.readAllLines(paymentFile, StandardCharsets.UTF_8);
            List<Payment> payments = new ArrayList<>();

            for (String line : lines) {
                if (line == null || line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                payments.add(parsePayment(line));
            }

            return payments;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read payments file", ex);
        }
    }

    private void writePayments(List<Payment> payments) {
        List<String> lines = new ArrayList<>();
        lines.add("# paymentId|paymentType|customerId|customerName|appointmentId|amount|status|paymentDate|cardNumber|cardHolderName|expiryDate|cardType|amountReceived|changeGiven");
        for (Payment payment : payments) {
            lines.add(toFileLine(payment));
        }

        try {
            Files.write(paymentFile, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write payments file", ex);
        }
    }

    private Payment parsePayment(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 14) {
            throw new IllegalStateException("Invalid payment record: " + line);
        }

        String paymentId = parts[0];
        String type = normalizeType(parts[1]);
        String customerId = parts[2];
        String customerName = parts[3];
        String appointmentId = parts[4];
        double amount = parseDouble(parts[5]);
        String status = normalizeStatus(parts[6]);
        String paymentDate = parts[7];

        if ("CARD".equals(type)) {
            return new CardPayment(paymentId, customerId, customerName, appointmentId,
                    amount, status, paymentDate, parts[8], parts[9], parts[10], parts[11]);
        }

        return new CashPayment(paymentId, customerId, customerName, appointmentId,
                amount, status, paymentDate, parseDouble(parts[12]), parseDouble(parts[13]));
    }

    private String toFileLine(Payment payment) {
        String cardNumber = "";
        String cardHolderName = "";
        String expiryDate = "";
        String cardType = "";
        String amountReceived = "";
        String changeGiven = "";

        if (payment instanceof CardPayment cardPayment) {
            cardNumber = clean(cardPayment.getCardNumber());
            cardHolderName = clean(cardPayment.getCardHolderName());
            expiryDate = clean(cardPayment.getExpiryDate());
            cardType = clean(cardPayment.getCardType());
        }

        if (payment instanceof CashPayment cashPayment) {
            amountReceived = formatMoney(cashPayment.getAmountReceived());
            changeGiven = formatMoney(cashPayment.getChangeGiven());
        }

        return String.join("|",
                clean(payment.getPaymentId()),
                clean(payment.getPaymentType()),
                clean(payment.getCustomerId()),
                clean(payment.getCustomerName()),
                clean(payment.getAppointmentId()),
                formatMoney(payment.getAmount()),
                clean(payment.getStatus()),
                clean(payment.getPaymentDate()),
                cardNumber,
                cardHolderName,
                expiryDate,
                cardType,
                amountReceived,
                changeGiven
        );
    }

    private void validateBasePayment(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment details are required");
        }
        if (isBlank(request.getCustomerId())) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (isBlank(request.getCustomerName())) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (isBlank(request.getAppointmentId())) {
            throw new IllegalArgumentException("Appointment ID is required");
        }
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        normalizeType(request.getPaymentType());
        normalizeStatus(defaultIfBlank(request.getStatus(), "COMPLETED"));
    }

    private void validateCardPayment(PaymentRequest request) {
        if (isBlank(request.getCardNumber())) {
            throw new IllegalArgumentException("Card number is required");
        }
        if (isBlank(request.getCardHolderName())) {
            throw new IllegalArgumentException("Card holder name is required");
        }
        if (isBlank(request.getExpiryDate())) {
            throw new IllegalArgumentException("Expiry date is required");
        }
    }

    private void validateCashPayment(PaymentRequest request) {
        if (request.getAmountReceived() < request.getAmount()) {
            throw new IllegalArgumentException("Amount received must be equal to or greater than the bill amount");
        }
    }

    private String normalizeType(String type) {
        String normalized = defaultIfBlank(type, "CASH").toUpperCase(Locale.ROOT);
        if (!"CARD".equals(normalized) && !"CASH".equals(normalized)) {
            throw new IllegalArgumentException("Payment type must be CARD or CASH");
        }
        return normalized;
    }

    private String normalizeStatus(String status) {
        String normalized = defaultIfBlank(status, "PENDING").toUpperCase(Locale.ROOT);
        for (String validStatus : VALID_STATUSES) {
            if (validStatus.equals(normalized)) {
                return normalized;
            }
        }
        throw new IllegalArgumentException("Status must be PENDING, COMPLETED, FAILED, or REFUNDED");
    }

    private double parseDouble(String value) {
        if (isBlank(value)) {
            return 0;
        }
        return Double.parseDouble(value);
    }

    private String formatMoney(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", " ").replace("\r", " ").replace("\n", " ").trim();
    }

    private String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : clean(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
