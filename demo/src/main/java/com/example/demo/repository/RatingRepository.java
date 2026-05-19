package com.example.demo.repository;

import com.example.demo.model.Rating;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * RatingRepository - File-based Data Access Layer
 * Singleton pattern, CRUD operations using pipe-delimited flat file
 */
public class RatingRepository {

    private static final String RATINGS_FILE = "data/ratings.txt";
    private static final String CSV_HEADER   = "id|userName|userEmail|serviceName|stars|comment|createdAt";

    private RatingRepository() {}
    private static RatingRepository instance;

    public static synchronized RatingRepository getInstance() {
        if (instance == null) {
            instance = new RatingRepository();
            instance.initializeFile();
        }
        return instance;
    }

    private void initializeFile() {
        try {
            Path path = Paths.get(RATINGS_FILE);
            if (path.getParent() != null && !Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());
            if (!Files.exists(path))
                Files.write(path, (CSV_HEADER + "\n").getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error initialising ratings file: " + e.getMessage());
        }
    }

    /** CREATE */
    public synchronized boolean save(Rating rating) throws IOException {
        if (rating == null) throw new IllegalArgumentException("Rating cannot be null");
        Path path = Paths.get(RATINGS_FILE);
        Files.write(path, (rating.toCSV() + "\n").getBytes(), StandardOpenOption.APPEND);
        return true;
    }

    /** READ ALL */
    public synchronized List<Rating> findAll() throws IOException {
        List<Rating> list = new ArrayList<>();
        Path path = Paths.get(RATINGS_FILE);
        if (!Files.exists(path)) return list;
        List<String> lines = Files.readAllLines(path);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            try { list.add(Rating.fromCSV(line)); }
            catch (Exception e) { System.err.println("Skipping bad line: " + e.getMessage()); }
        }
        // newest first
        list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return list;
    }

    /** READ BY SERVICE */
    public synchronized List<Rating> findByService(String serviceName) throws IOException {
        List<Rating> all = findAll();
        List<Rating> result = new ArrayList<>();
        for (Rating r : all)
            if (r.getServiceName().equalsIgnoreCase(serviceName)) result.add(r);
        return result;
    }

    /** READ BY ID */
    public synchronized Rating findById(String id) throws IOException {
        for (Rating r : findAll())
            if (r.getId().equals(id)) return r;
        return null;
    }

    /** DELETE by ID */
    public synchronized boolean delete(String id) throws IOException {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID cannot be empty");
        Path path = Paths.get(RATINGS_FILE);
        List<String> lines = Files.readAllLines(path);
        List<String> updated = new ArrayList<>();
        updated.add(lines.get(0)); // header
        boolean found = false;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) { updated.add(line); continue; }
            try {
                Rating r = Rating.fromCSV(line);
                if (r.getId().equals(id)) { found = true; }
                else { updated.add(lines.get(i)); }
            } catch (Exception e) { updated.add(lines.get(i)); }
        }
        if (!found) throw new IllegalArgumentException("Rating not found");
        Files.write(path, updated, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        return true;
    }
}
