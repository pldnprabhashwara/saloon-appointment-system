package com.example.demo.repository;

import com.example.demo.model.User;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * UserRepository - File-based Data Access Layer
 * Demonstrates OOP Concepts: Abstraction, Encapsulation, Information Hiding
 * Handles CRUD operations: Create, Read, Update, Delete
 */
public class UserRepository {
    
    private static final String USERS_FILE_PATH = "data/users.txt";
    private static final String CSV_HEADER = "name,phone,email,password,registeredAt,lastUpdatedAt";
    
    // Private constructor to prevent instantiation - Singleton pattern
    private UserRepository() {
    }
    
    // Static instance for singleton
    private static UserRepository instance;
    
    /**
     * Get singleton instance - Lazy initialization
     */
    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
            instance.initializeFile();
        }
        return instance;
    }
    
    /**
     * Initialize users.txt file if it doesn't exist
     * Private method - Information Hiding
     */
    private void initializeFile() {
        try {
            Path filePath = Paths.get(USERS_FILE_PATH);
            Path parentDir = filePath.getParent();
            
            // Create directories if they don't exist
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            
            // Create file if it doesn't exist
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                // Write CSV header
                Files.write(filePath, (CSV_HEADER + "\n").getBytes(), StandardOpenOption.WRITE);
            }
        } catch (IOException e) {
            System.err.println("Error initializing users file: " + e.getMessage());
        }
    }
    
    /**
     * CREATE - Register a new user
     * @param user User object to register
     * @return true if registration successful, false if email already exists
     * @throws IOException if file operation fails
     */
    public synchronized boolean save(User user) throws IOException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        // Check if email already exists
        if (findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        try {
            Path filePath = Paths.get(USERS_FILE_PATH);
            String csvLine = user.toCSV() + "\n";
            Files.write(filePath, csvLine.getBytes(), StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            throw new IOException("Error saving user: " + e.getMessage());
        }
    }
    
    /**
     * READ - Find user by email
     * @param email User email
     * @return User object if found, null otherwise
     * @throws IOException if file operation fails
     */
    public User findByEmail(String email) throws IOException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        try {
            Path filePath = Paths.get(USERS_FILE_PATH);
            
            if (!Files.exists(filePath)) {
                return null;
            }
            
            List<String> lines = Files.readAllLines(filePath);
            
            // Skip header line and find user
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                try {
                    User user = User.fromCSV(line);
                    if (user.getEmail().equalsIgnoreCase(email)) {
                        return user;
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing user data: " + e.getMessage());
                }
            }
            
            return null;
        } catch (IOException e) {
            throw new IOException("Error reading users: " + e.getMessage());
        }
    }
    
    /**
     * READ - Get all users
     * @return List of all users
     * @throws IOException if file operation fails
     */
    public synchronized List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();
        
        try {
            Path filePath = Paths.get(USERS_FILE_PATH);
            
            if (!Files.exists(filePath)) {
                return users;
            }
            
            List<String> lines = Files.readAllLines(filePath);
            
            // Skip header line
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                try {
                    User user = User.fromCSV(line);
                    users.add(user);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing user data: " + e.getMessage());
                }
            }
            
            return users;
        } catch (IOException e) {
            throw new IOException("Error reading all users: " + e.getMessage());
        }
    }
    
    /**
     * UPDATE - Update user information
     * @param updatedUser Updated user object
     * @return true if update successful, false if user not found
     * @throws IOException if file operation fails
     */
    public synchronized boolean update(User updatedUser) throws IOException {
        if (updatedUser == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        try {
            Path filePath = Paths.get(USERS_FILE_PATH);
            List<String> lines = Files.readAllLines(filePath);
            boolean found = false;
            
            // Find and update user
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                try {
                    User user = User.fromCSV(line);
                    if (user.getEmail().equalsIgnoreCase(updatedUser.getEmail())) {
                        lines.set(i, updatedUser.toCSV());
                        found = true;
                        break;
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing user data: " + e.getMessage());
                }
            }
            
            if (!found) {
                throw new IllegalArgumentException("User not found");
            }
            
            // Write updated lines back to file
            Files.write(filePath, lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            throw new IOException("Error updating user: " + e.getMessage());
        }
    }
    
    /**
     * DELETE - Remove user by email
     * @param email User email
     * @return true if deletion successful, false if user not found
     * @throws IOException if file operation fails
     */
    public synchronized boolean delete(String email) throws IOException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        try {
            Path filePath = Paths.get(USERS_FILE_PATH);
            List<String> lines = Files.readAllLines(filePath);
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;
            
            // Keep header
            updatedLines.add(lines.get(0));
            
            // Filter out the user to delete
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    updatedLines.add(line);
                    continue;
                }
                
                try {
                    User user = User.fromCSV(line);
                    if (!user.getEmail().equalsIgnoreCase(email)) {
                        updatedLines.add(lines.get(i));
                    } else {
                        found = true;
                    }
                } catch (IllegalArgumentException e) {
                    updatedLines.add(lines.get(i)); // Keep problematic lines
                    System.err.println("Error parsing user data: " + e.getMessage());
                }
            }
            
            if (!found) {
                throw new IllegalArgumentException("User not found");
            }
            
            // Write updated lines back to file
            Files.write(filePath, updatedLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            throw new IOException("Error deleting user: " + e.getMessage());
        }
    }
    
    /**
     * Verify user login credentials
     * @param email User email
     * @param password User password
     * @return User object if credentials are valid, null otherwise
     * @throws IOException if file operation fails
     */
    public User authenticate(String email, String password) throws IOException {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password cannot be null");
        }
        
        User user = findByEmail(email);
        
        if (user != null && user.verifyPassword(password)) {
            return user;
        }
        
        return null;
    }
}
