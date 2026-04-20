package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import java.io.IOException;
import java.util.List;

/**
 * UserService - Business Logic Layer
 * Demonstrates OOP Concepts: Abstraction, Encapsulation
 * Service pattern for handling user operations
 */
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * Constructor with dependency injection
     */
    public UserService() {
        this.userRepository = UserRepository.getInstance();
    }
    
    /**
     * Register a new user
     * @param name User name
     * @param phone User phone number
     * @param email User email
     * @param password User password
     * @return true if registration successful
     * @throws IllegalArgumentException if validation fails
     * @throws IOException if file operation fails
     */
    public boolean registerUser(String name, String phone, String email, String password) throws Exception {
        try {
            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            if (phone == null || !phone.matches("\\d{10}")) {
                throw new IllegalArgumentException("Phone must be exactly 10 digits");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }
            
            // Create user object with validation
            User user = new User(name, phone, email, password);
            
            // Save to repository
            return userRepository.save(user);
        } catch (IOException e) {
            throw new IOException("Error during registration: " + e.getMessage());
        }
    }
    
    /**
     * Authenticate user with email and password
     * @param email User email
     * @param password User password
     * @return User object if authentication successful, null otherwise
     * @throws IOException if file operation fails
     */
    public User loginUser(String email, String password) throws IOException {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }
            
            User user = userRepository.authenticate(email, password);
            
            if (user == null) {
                throw new IllegalArgumentException("Invalid email or password");
            }
            
            return user;
        } catch (IOException e) {
            throw new IOException("Error during login: " + e.getMessage());
        }
    }
    
    /**
     * Get user by email
     * @param email User email
     * @return User object if found
     * @throws IOException if file operation fails
     */
    public User getUserByEmail(String email) throws IOException {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            
            User user = userRepository.findByEmail(email);
            
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            
            return user;
        } catch (IOException e) {
            throw new IOException("Error retrieving user: " + e.getMessage());
        }
    }
    
    /**
     * Update user profile
     * @param email User email (to find the user)
     * @param name New name
     * @param phone New phone
     * @param password New password (optional - if empty, keep old password)
     * @return true if update successful
     * @throws IOException if file operation fails
     */
    public boolean updateUserProfile(String email, String name, String phone, String password) throws Exception {
        try {
            // Validate inputs
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            
            // Get existing user
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            
            // Update fields
            if (name != null && !name.trim().isEmpty()) {
                user.setName(name);
            }
            
            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone);
            }
            
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password);
            }
            
            // Save updated user
            return userRepository.update(user);
        } catch (IOException e) {
            throw new IOException("Error updating profile: " + e.getMessage());
        }
    }
    
    /**
     * Delete user account
     * @param email User email
     * @return true if deletion successful
     * @throws IOException if file operation fails
     */
    public boolean deleteUserAccount(String email) throws IOException {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            
            // Verify user exists
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            
            return userRepository.delete(email);
        } catch (IOException e) {
            throw new IOException("Error deleting account: " + e.getMessage());
        }
    }
    
    /**
     * Get all users (admin functionality)
     * @return List of all users
     * @throws IOException if file operation fails
     */
    public List<User> getAllUsers() throws IOException {
        try {
            return userRepository.findAll();
        } catch (IOException e) {
            throw new IOException("Error retrieving users: " + e.getMessage());
        }
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if password meets requirements
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*].*");
        
        return hasUppercase && hasNumber && hasSpecialChar;
    }
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if email format is valid
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }
    
    /**
     * Validate phone format
     * @param phone Phone to validate
     * @return true if phone is exactly 10 digits
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }
}
