package com.example.demo.repository;

import com.example.demo.model.Stylist;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File based data access layer for Stylist CRUD operations.
 */
public class StylistRepository {
    private static final String STYLIST_FILE_PATH = "data/stylists.txt";
    private static final String CSV_HEADER = "stylistId,stylistName,specialization,availability";

    private static StylistRepository instance;

    private StylistRepository() {
    }

    public static synchronized StylistRepository getInstance() {
        if (instance == null) {
            instance = new StylistRepository();
            instance.initializeFile();
        }
        return instance;
    }

    private void initializeFile() {
        try {
            Path filePath = Paths.get(STYLIST_FILE_PATH);
            Path parentDir = filePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                Files.writeString(filePath, CSV_HEADER + System.lineSeparator(), StandardOpenOption.WRITE);
                seedDefaultStylists();
            }
        } catch (IOException e) {
            System.err.println("Error initializing stylist file: " + e.getMessage());
        }
    }

    private void seedDefaultStylists() throws IOException {
        save(new Stylist("ST001", "Amanda Silva", "Hair Cutting", true));
        save(new Stylist("ST002", "Kavindi Perera", "Hair Coloring", true));
        save(new Stylist("ST003", "Nimal Fernando", "Bridal Styling", false));
    }

    public synchronized boolean save(Stylist stylist) throws IOException {
        if (stylist == null) {
            throw new IllegalArgumentException("Stylist cannot be null");
        }
        if (findById(stylist.getStylistId()) != null) {
            throw new IllegalArgumentException("Stylist ID already exists");
        }
        Files.writeString(Paths.get(STYLIST_FILE_PATH), stylist.toCSV() + System.lineSeparator(), StandardOpenOption.APPEND);
        return true;
    }

    public synchronized List<Stylist> findAll() throws IOException {
        initializeFile();
        List<Stylist> stylists = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(STYLIST_FILE_PATH));
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!line.isEmpty()) {
                try {
                    stylists.add(Stylist.fromCSV(line));
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing stylist data: " + e.getMessage());
                }
            }
        }
        return stylists;
    }

    public synchronized Stylist findById(String stylistId) throws IOException {
        if (stylistId == null || stylistId.trim().isEmpty()) {
            return null;
        }
        for (Stylist stylist : findAll()) {
            if (stylist.getStylistId().equalsIgnoreCase(stylistId.trim())) {
                return stylist;
            }
        }
        return null;
    }

    public synchronized boolean update(Stylist updatedStylist) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(STYLIST_FILE_PATH));
        boolean found = false;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            Stylist stylist = Stylist.fromCSV(line);
            if (stylist.getStylistId().equalsIgnoreCase(updatedStylist.getStylistId())) {
                lines.set(i, updatedStylist.toCSV());
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Stylist not found");
        }

        Files.write(Paths.get(STYLIST_FILE_PATH), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        return true;
    }

    public synchronized boolean delete(String stylistId) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(STYLIST_FILE_PATH));
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        updatedLines.add(lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            Stylist stylist = Stylist.fromCSV(line);
            if (stylist.getStylistId().equalsIgnoreCase(stylistId.trim())) {
                found = true;
            } else {
                updatedLines.add(line);
            }
        }

        Files.write(Paths.get(STYLIST_FILE_PATH), updatedLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        return found;
    }
}
