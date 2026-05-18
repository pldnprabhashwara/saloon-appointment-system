package com.example.demo.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * User Model Class - Encapsulation of User Data
 * Demonstrates OOP Concepts: Encapsulation, Information Hiding
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String phone;
    private String email;
    private String password;
    private LocalDateTime registeredAt;
    private LocalDateTime lastUpdatedAt;
    
    // Private constants for validation
    private static final int MIN_NAME_LENGTH = 2;
    private static final int PHONE_LENGTH = 10;
    private static final int MIN_PASSWORD_LENGTH = 8;
    
    /**
     * Default Constructor
     */
    public User() {
        this.registeredAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized Constructor
     */
    public User(String name, String phone, String email, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.registeredAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    // ==================== GETTERS ====================
    
    /**
     * Get user name - Encapsulation
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get user phone - Encapsulation
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Get user email - Encapsulation
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Get user password - Information Hiding
     * Password access is limited to this class
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Get registration timestamp
     */
    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
    
    /**
     * Get last updated timestamp
     */
    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }
    
    // ==================== SETTERS ====================
    
    /**
     * Set user name with validation - Encapsulation
     */
    public void setName(String name) throws IllegalArgumentException {
        if (name == null || name.trim().length() < MIN_NAME_LENGTH) {
            throw new IllegalArgumentException("Name must be at least " + MIN_NAME_LENGTH + " characters");
        }
        this.name = name.trim();
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    /**
     * Set user phone with validation - Encapsulation
     */
    public void setPhone(String phone) throws IllegalArgumentException {
        if (phone == null || !phone.matches("\\d{" + PHONE_LENGTH + "}")) {
            throw new IllegalArgumentException("Phone must be exactly " + PHONE_LENGTH + " digits");
        }
        this.phone = phone;
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    /**
     * Set user email with validation - Encapsulation
     */
    public void setEmail(String email) throws IllegalArgumentException {
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.toLowerCase().trim();
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    /**
     * Set user password with validation - Information Hiding
     */
    public void setPassword(String password) throws IllegalArgumentException {
        if (!isPasswordStrong(password)) {
            throw new IllegalArgumentException("Password must contain at least 8 characters, one uppercase letter, one number, and one special character");
        }
        this.password = password;
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    /**
     * Private method to validate password strength - Information Hiding
     * Abstraction of validation logic
     */
    private static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*].*");
        
        return hasUppercase && hasNumber && hasSpecialChar;
    }
    
    // ==================== BUSINESS LOGIC METHODS ====================
    
    /**
     * Verify if password matches - Abstraction of comparison logic
     */
    public boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    /**
     * Convert user to CSV format for file storage
     * Abstraction of serialization logic
     */
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%s",
                name.replace(",", "\\,"),
                phone,
                email,
                password,
                registeredAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                lastUpdatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    /**
     * Create User object from CSV string
     * Abstraction of deserialization logic
     */
    public static User fromCSV(String csvLine) throws IllegalArgumentException {
        String[] parts = csvLine.split(",(?=(?:[^\\\\]|\\\\.)*$)"); // Handle escaped commas
        
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid CSV format");
        }
        
        User user = new User();
        user.setName(parts[0].replace("\\,", ","));
        user.setPhone(parts[1]);
        user.setEmail(parts[2]);
        user.password = parts[3]; // Set password directly without validation
        
        if (parts.length >= 5) {
            try {
                user.registeredAt = LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                user.registeredAt = LocalDateTime.now();
            }
        }
        
        if (parts.length >= 6) {
            try {
                user.lastUpdatedAt = LocalDateTime.parse(parts[5], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                user.lastUpdatedAt = LocalDateTime.now();
            }
        }
        
        return user;
    }
    
    /**
     * String representation of User - For debugging
     */
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", registeredAt=" + registeredAt +
                ", lastUpdatedAt=" + lastUpdatedAt +
                '}';
    }
    
    /**
     * Check equality based on email - Unique identifier
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        return this.email.equalsIgnoreCase(other.email);
    }
    
    /**
     * Hash code based on email
     */
    @Override
    public int hashCode() {
        return email.toLowerCase().hashCode();
    }
}
