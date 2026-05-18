package com.example.demo.repository;

import com.example.demo.model.Review;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewRepository {
    private static final String FILE_PATH = "data/reviews.txt";

    public ReviewRepository() {
        try {
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(Review review) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(review.toCSV());
            writer.newLine();
        }
    }

    public List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    reviews.add(Review.fromCSV(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
