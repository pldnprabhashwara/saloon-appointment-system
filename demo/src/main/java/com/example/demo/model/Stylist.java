package com.example.demo.model;

import java.io.Serializable;

/**
 * Stylist Model Class
 * Variables requested for the Stylist module:
 * stylistId, stylistName, specialization, availability
 */
public class Stylist implements Serializable {
    private static final long serialVersionUID = 1L;

    private String stylistId;
    private String stylistName;
    private String specialization;
    private boolean availability;

    public Stylist() {
    }

    public Stylist(String stylistId, String stylistName, String specialization, boolean availability) {
        this.stylistId = stylistId;
        this.stylistName = stylistName;
        this.specialization = specialization;
        this.availability = availability;
    }

    public String getStylistId() {
        return stylistId;
    }

    public String getStylistName() {
        return stylistName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public boolean isAvailable() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    // Extra setters are used only for form binding / file loading.
    public void setStylistId(String stylistId) {
        if (stylistId == null || stylistId.trim().isEmpty()) {
            throw new IllegalArgumentException("Stylist ID cannot be empty");
        }
        this.stylistId = stylistId.trim();
    }

    public void setStylistName(String stylistName) {
        if (stylistName == null || stylistName.trim().isEmpty()) {
            throw new IllegalArgumentException("Stylist name cannot be empty");
        }
        this.stylistName = stylistName.trim();
    }

    public void setSpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization cannot be empty");
        }
        this.specialization = specialization.trim();
    }

    public String getAvailabilityText() {
        return availability ? "Available" : "Not Available";
    }

    public String toCSV() {
        return String.format("%s,%s,%s,%s",
                escape(stylistId),
                escape(stylistName),
                escape(specialization),
                availability);
    }

    public static Stylist fromCSV(String csvLine) {
        String[] parts = csvLine.split(",(?=(?:[^\\\\]|\\\\.)*$)");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid stylist CSV format");
        }

        Stylist stylist = new Stylist();
        stylist.setStylistId(unescape(parts[0]));
        stylist.setStylistName(unescape(parts[1]));
        stylist.setSpecialization(unescape(parts[2]));
        stylist.setAvailability(Boolean.parseBoolean(parts[3]));
        return stylist;
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace(",", "\\,");
    }

    private static String unescape(String value) {
        return value == null ? "" : value.replace("\\,", ",");
    }
}
