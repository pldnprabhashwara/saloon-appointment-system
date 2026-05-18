package com.example.demo.service;

import com.example.demo.model.Review;
import com.example.demo.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void saveReview(Review review) throws IOException {
        reviewRepository.save(review);
    }

    public List<Review> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        // Return latest first
        Collections.reverse(reviews);
        return reviews;
    }

    public List<Review> getReviewsByService(String service) {
        if (service == null || service.equalsIgnoreCase("All Services") || service.isEmpty()) {
            return getAllReviews();
        }
        return getAllReviews().stream()
                .filter(r -> r.getServiceReceived().equalsIgnoreCase(service))
                .collect(Collectors.toList());
    }
}
