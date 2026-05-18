package com.example.demo.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service Model Class
 * OOP Concept: Encapsulation - All fields are private, accessed via getters/setters
 * Represents a salon service (e.g., Haircut, Coloring, Facial)
 */
public class Service implements Serializable {
    private static final long serialVersionUID = 1L;

    // Private fields - Encapsulation (Information Hiding)
    private String serviceId;
    private String serviceName;
    private String category;       // e.g., Hair, Skin, Nail
    private double price;          // Private - cannot be accessed directly
    private int durationMinutes;   // Private - controlled access only
    private String status;         // Active / Inactive
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constants - validation rules hidden inside class
    private static final double MIN_PRICE = 0.0;
    private static final int MIN_DURATION = 5;
    private static final int MAX_DURATION = 480;

    /**
     * Default Constructor
     */
    public Service() {
        this.status = "Active";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Parameterized Constructor
     */
    public Service(String serviceId, String serviceName, String category,
                   double price, int durationMinutes, String description) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.category = category;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.description = description;
        this.status = "Active";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== GETTERS (Encapsulation) ====================

    public String getServiceId() { return serviceId; }
    public String getServiceName() { return serviceName; }
    public String getCategory() { return category; }

    /**
     * Get price - Encapsulation: price field is private
     */
    public double getPrice() { return price; }

    /**
     * Get formatted price as string (e.g., "Rs. 1500.00")
     * Abstraction of formatting logic
     */
    public String getFormattedPrice() {
        return String.format("Rs. %.2f", price);
    }

    public int getDurationMinutes() { return durationMinutes; }

    /**
     * Get formatted duration (e.g., "1h 30m")
     * Abstraction of conversion logic
     */
    public String getFormattedDuration() {
        if (durationMinutes < 60) {
            return durationMinutes + " mins";
        }
        int hours = durationMinutes / 60;
        int mins = durationMinutes % 60;
        return mins > 0 ? hours + "h " + mins + "m" : hours + "h";
    }

    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public boolean isActive() { return "Active".equalsIgnoreCase(status); }

    // ==================== SETTERS with Validation (Encapsulation) ====================

    public void setServiceId(String serviceId) {
        if (serviceId == null || serviceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Service ID cannot be empty");
        }
        this.serviceId = serviceId.trim();
    }

    public void setServiceName(String serviceName) {
        if (serviceName == null || serviceName.trim().length() < 2) {
            throw new IllegalArgumentException("Service name must be at least 2 characters");
        }
        this.serviceName = serviceName.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        this.category = category.trim();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set price with validation - Encapsulation protects price field
     */
    public void setPrice(double price) {
        if (price < MIN_PRICE) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set duration with validation - Encapsulation protects durationMinutes field
     */
    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes < MIN_DURATION || durationMinutes > MAX_DURATION) {
            throw new IllegalArgumentException(
                "Duration must be between " + MIN_DURATION + " and " + MAX_DURATION + " minutes"
            );
        }
        this.durationMinutes = durationMinutes;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStatus(String status) {
        if (!"Active".equalsIgnoreCase(status) && !"Inactive".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Status must be Active or Inactive");
        }
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== FILE STORAGE METHODS ====================

    /**
     * Convert to CSV line for file storage
     * Abstraction: caller doesn't need to know the format
     */
    public String toCSV() {
        return String.format("%s|%s|%s|%.2f|%d|%s|%s|%s|%s",
            serviceId,
            serviceName.replace("|", "\\|"),
            category,
            price,
            durationMinutes,
            status,
            description.replace("|", "\\|"),
            createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            updatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    /**
     * Create Service from a CSV line
     * Abstraction: parsing logic is hidden
     */
    public static Service fromCSV(String csvLine) {
        String[] parts = csvLine.split("\\|");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid CSV format for service: " + csvLine);
        }

        Service s = new Service();
        s.serviceId = parts[0];
        s.serviceName = parts[1].replace("\\|", "|");
        s.category = parts[2];
        s.price = Double.parseDouble(parts[3]);
        s.durationMinutes = Integer.parseInt(parts[4]);
        s.status = parts[5];
        s.description = parts.length > 6 ? parts[6].replace("\\|", "|") : "";

        if (parts.length > 7) {
            try { s.createdAt = LocalDateTime.parse(parts[7], DateTimeFormatter.ISO_LOCAL_DATE_TIME); }
            catch (Exception e) { s.createdAt = LocalDateTime.now(); }
        } else {
            s.createdAt = LocalDateTime.now();
        }

        if (parts.length > 8) {
            try { s.updatedAt = LocalDateTime.parse(parts[8], DateTimeFormatter.ISO_LOCAL_DATE_TIME); }
            catch (Exception e) { s.updatedAt = LocalDateTime.now(); }
        } else {
            s.updatedAt = LocalDateTime.now();
        }

        return s;
    }

    @Override
    public String toString() {
        return "Service{id='" + serviceId + "', name='" + serviceName +
               "', category='" + category + "', price=" + price +
               ", duration=" + durationMinutes + "min, status='" + status + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Service)) return false;
        return this.serviceId.equalsIgnoreCase(((Service) obj).serviceId);
    }

    @Override
    public int hashCode() {
        return serviceId.toLowerCase().hashCode();
    }
}
