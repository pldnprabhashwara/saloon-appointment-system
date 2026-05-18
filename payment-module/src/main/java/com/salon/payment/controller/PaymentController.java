package com.salon.payment.controller;

import com.salon.payment.dto.PaymentReport;
import com.salon.payment.dto.PaymentRequest;
import com.salon.payment.dto.StatusUpdateRequest;
import com.salon.payment.model.Payment;
import com.salon.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> makePayment(@RequestBody PaymentRequest request) {
        Payment payment = paymentService.makePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "payment", payment,
                "processingMessage", payment.processPayment()
        ));
    }

    @GetMapping
    public List<Payment> viewPaymentHistory() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{paymentId}")
    public Payment getPayment(@PathVariable String paymentId) {
        return paymentService.findPayment(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found: " + paymentId));
    }

    @PatchMapping("/{paymentId}/status")
    public Payment updatePaymentStatus(@PathVariable String paymentId,
                                       @RequestBody StatusUpdateRequest request) {
        return paymentService.updatePaymentStatus(paymentId, request.getStatus());
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePaymentRecord(@PathVariable String paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report")
    public PaymentReport getPaymentReport() {
        return paymentService.buildReport();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleFileError(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
    }
}
