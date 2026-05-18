package com.example.demo.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Review implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String id;
    private String name;
    private String email;
    private String serviceReceived;
    private int starRating;
    private String comment;
    private LocalDateTime submittedAt;

    public Review() {
        this.id = UUID.randomUUID().toString();
        this.submittedAt = LocalDateTime.now();
    }

    public Review(String name, String email, String serviceReceived, int starRating, String comment) {
        this();
        this.name = name;
        this.email = email;
        this.serviceReceived = serviceReceived;
        this.starRating = starRating;
        this.comment = comment.replace("\n", " ").replace(",", ";"); // sanitize for CSV
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getServiceReceived() { return serviceReceived; }
    public void setServiceReceived(String serviceReceived) { this.serviceReceived = serviceReceived; }
    public int getStarRating() { return starRating; }
    public void setStarRating(int starRating) { this.starRating = starRating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String toCSV() {
        return String.join(",",
                id,
                name,
                email,
                serviceReceived,
                String.valueOf(starRating),
                comment,
                submittedAt.format(formatter)
        );
    }

    public static Review fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 7) throw new IllegalArgumentException("Invalid CSV for Review");

        Review review = new Review();
        review.setId(parts[0]);
        review.setName(parts[1]);
        review.setEmail(parts[2]);
        review.setServiceReceived(parts[3]);
        review.setStarRating(Integer.parseInt(parts[4]));
        review.setComment(parts[5]);
        review.setSubmittedAt(LocalDateTime.parse(parts[6], formatter));
        return review;
    }
}
