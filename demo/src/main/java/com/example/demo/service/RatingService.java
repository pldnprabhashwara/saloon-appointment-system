package com.example.demo.service;

import com.example.demo.model.Rating;
import com.example.demo.repository.RatingRepository;
import java.io.IOException;
import java.util.List;

/**
 * RatingService - Business Logic Layer
 * Abstraction of rating operations
 */
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService() {
        this.ratingRepository = RatingRepository.getInstance();
    }

    /**
     * Submit a new rating
     */
    public boolean submitRating(String userName, String userEmail, String serviceName, int stars, String comment) throws Exception {
        if (userName == null || userName.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        if (userEmail == null || !userEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Invalid email format");
        if (serviceName == null || serviceName.trim().isEmpty())
            throw new IllegalArgumentException("Service name cannot be empty");
        if (stars < 1 || stars > 5)
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        if (comment == null || comment.trim().isEmpty())
            throw new IllegalArgumentException("Comment cannot be empty");

        Rating rating = new Rating(userName, userEmail, serviceName, stars, comment);
        return ratingRepository.save(rating);
    }

    /** Get all ratings, newest first */
    public List<Rating> getAllRatings() throws IOException {
        return ratingRepository.findAll();
    }

    /** Get ratings for a specific service */
    public List<Rating> getRatingsByService(String serviceName) throws IOException {
        return ratingRepository.findByService(serviceName);
    }

    /** Delete a rating by ID */
    public boolean deleteRating(String id) throws IOException {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("Rating ID cannot be empty");
        return ratingRepository.delete(id);
    }

    /**
     * Calculate average stars for a service
     */
    public double getAverageStars(String serviceName) throws IOException {
        List<Rating> ratings = ratingRepository.findByService(serviceName);
        if (ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToInt(Rating::getStars).average().orElse(0.0);
    }

    /** Get total rating count */
    public int getTotalCount() throws IOException {
        return ratingRepository.findAll().size();
    }
}
