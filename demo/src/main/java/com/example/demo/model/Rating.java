package com.example.demo.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Rating Model Class - Encapsulation of Rating Data
 * Demonstrates OOP Concepts: Encapsulation, Information Hiding
 */
public class Rating implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userName;
    private String userEmail;
    private String serviceName;
    private int stars;
    private String comment;
    private LocalDateTime createdAt;

    private static final int MIN_STARS = 1;
    private static final int MAX_STARS = 5;

    public Rating() {
        this.id        = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public Rating(String userName, String userEmail, String serviceName, int stars, String comment) {
        this.id          = UUID.randomUUID().toString();
        this.userName    = userName;
        this.userEmail   = userEmail;
        this.serviceName = serviceName;
        this.stars       = stars;
        this.comment     = comment;
        this.createdAt   = LocalDateTime.now();
    }

    public String getId()          { return id; }
    public String getUserName()    { return userName; }
    public String getUserEmail()   { return userEmail; }
    public String getServiceName() { return serviceName; }
    public int    getStars()       { return stars; }
    public String getComment()     { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUserName(String userName) {
        if (userName == null || userName.trim().isEmpty())
            throw new IllegalArgumentException("User name cannot be empty");
        this.userName = userName.trim();
    }

    public void setUserEmail(String userEmail) {
        if (userEmail == null || !userEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Invalid email format");
        this.userEmail = userEmail.toLowerCase().trim();
    }

    public void setServiceName(String serviceName) {
        if (serviceName == null || serviceName.trim().isEmpty())
            throw new IllegalArgumentException("Service name cannot be empty");
        this.serviceName = serviceName.trim();
    }

    public void setStars(int stars) {
        if (stars < MIN_STARS || stars > MAX_STARS)
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        this.stars = stars;
    }

    public void setComment(String comment) {
        if (comment == null || comment.trim().isEmpty())
            throw new IllegalArgumentException("Comment cannot be empty");
        this.comment = comment.trim();
    }

    public String toCSV() {
        return String.format("%s|%s|%s|%s|%d|%s|%s",
                id,
                userName.replace("|", "\\|"),
                userEmail,
                serviceName.replace("|", "\\|"),
                stars,
                comment.replace("|", "\\|").replace("\n", "\\n"),
                createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public static Rating fromCSV(String csvLine) {
        String[] parts = csvLine.split("\\|(?=(?:[^\\\\]|\\\\.)*$)");
        if (parts.length < 7)
            throw new IllegalArgumentException("Invalid CSV format: " + csvLine);
        Rating r = new Rating();
        r.id          = parts[0];
        r.userName    = parts[1].replace("\\|", "|");
        r.userEmail   = parts[2];
        r.serviceName = parts[3].replace("\\|", "|");
        r.stars       = Integer.parseInt(parts[4].trim());
        r.comment     = parts[5].replace("\\|", "|").replace("\\n", "\n");
        try { r.createdAt = LocalDateTime.parse(parts[6], DateTimeFormatter.ISO_LOCAL_DATE_TIME); }
        catch (Exception e) { r.createdAt = LocalDateTime.now(); }
        return r;
    }

    @Override
    public String toString() {
        return "Rating{id='" + id + "', user='" + userName + "', service='" + serviceName + "', stars=" + stars + "}";
    }
}
