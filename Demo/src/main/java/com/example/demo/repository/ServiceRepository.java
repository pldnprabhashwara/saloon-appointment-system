package com.example.demo.repository;

import com.example.demo.model.Service;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ServiceRepository {

    private static final String SERVICES_FILE_PATH = "data/services.txt";
    private static final String FILE_HEADER = "serviceId|serviceName|category|price|durationMinutes|status|description|createdAt|updatedAt";

    // Singleton pattern - same as UserRepository
    private static ServiceRepository instance;

    private ServiceRepository() {}

    public static synchronized ServiceRepository getInstance() {
        if (instance == null) {
            instance = new ServiceRepository();
            instance.initializeFile();
        }
        return instance;
    }

    private void initializeFile() {
        try {
            Path filePath = Paths.get(SERVICES_FILE_PATH);
            Path parentDir = filePath.getParent();

            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                Files.write(filePath, (FILE_HEADER + "\n").getBytes(), StandardOpenOption.WRITE);
            }
        } catch (IOException e) {
            System.err.println("Error initializing services file: " + e.getMessage());
        }
    }

    public synchronized boolean save(Service service) throws IOException {
        if (service == null) throw new IllegalArgumentException("Service cannot be null");

        if (findById(service.getServiceId()) != null) {
            throw new IllegalArgumentException("Service ID already exists: " + service.getServiceId());
        }

        Path filePath = Paths.get(SERVICES_FILE_PATH);
        Files.write(filePath, (service.toCSV() + "\n").getBytes(), StandardOpenOption.APPEND);
        return true;
    }

    public Service findById(String serviceId) throws IOException {
        if (serviceId == null || serviceId.trim().isEmpty())
            throw new IllegalArgumentException("Service ID cannot be empty");

        Path filePath = Paths.get(SERVICES_FILE_PATH);
        if (!Files.exists(filePath)) return null;

        List<String> lines = Files.readAllLines(filePath);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            try {
                Service s = Service.fromCSV(line);
                if (s.getServiceId().equalsIgnoreCase(serviceId)) return s;
            } catch (Exception e) {
                System.err.println("Error parsing line: " + e.getMessage());
            }
        }
        return null;
    }

    public synchronized List<Service> findAll() throws IOException {
        List<Service> services = new ArrayList<>();
        Path filePath = Paths.get(SERVICES_FILE_PATH);
        if (!Files.exists(filePath)) return services;

        List<String> lines = Files.readAllLines(filePath);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            try {
                services.add(Service.fromCSV(line));
            } catch (Exception e) {
                System.err.println("Error parsing service: " + e.getMessage());
            }
        }
        return services;
    }

    public List<Service> findByCategory(String category) throws IOException {
        List<Service> result = new ArrayList<>();
        for (Service s : findAll()) {
            if (s.getCategory().equalsIgnoreCase(category)) result.add(s);
        }
        return result;
    }

    public List<Service> findAllActive() throws IOException {
        List<Service> result = new ArrayList<>();
        for (Service s : findAll()) {
            if (s.isActive()) result.add(s);
        }
        return result;
    }

    public List<Service> searchByName(String keyword) throws IOException {
        List<Service> result = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Service s : findAll()) {
            if (s.getServiceName().toLowerCase().contains(lower)) result.add(s);
        }
        return result;
    }

    public synchronized boolean update(Service updatedService) throws IOException {
        if (updatedService == null) throw new IllegalArgumentException("Service cannot be null");

        Path filePath = Paths.get(SERVICES_FILE_PATH);
        List<String> lines = Files.readAllLines(filePath);
        boolean found = false;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            try {
                Service s = Service.fromCSV(line);
                if (s.getServiceId().equalsIgnoreCase(updatedService.getServiceId())) {
                    lines.set(i, updatedService.toCSV());
                    found = true;
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error parsing service: " + e.getMessage());
            }
        }

        if (!found) throw new IllegalArgumentException("Service not found: " + updatedService.getServiceId());

        Files.write(filePath, lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        return true;
    }

    public synchronized boolean delete(String serviceId) throws IOException {
        if (serviceId == null || serviceId.trim().isEmpty())
            throw new IllegalArgumentException("Service ID cannot be empty");

        Path filePath = Paths.get(SERVICES_FILE_PATH);
        List<String> lines = Files.readAllLines(filePath);
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        updatedLines.add(lines.get(0)); // keep header

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                updatedLines.add(line);
                continue;
            }
            try {
                Service s = Service.fromCSV(line);
                if (!s.getServiceId().equalsIgnoreCase(serviceId)) {
                    updatedLines.add(lines.get(i));
                } else {
                    found = true;
                }
            } catch (Exception e) {
                updatedLines.add(lines.get(i));
            }
        }

        if (!found) throw new IllegalArgumentException("Service not found: " + serviceId);

        Files.write(filePath, updatedLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        return true;
    }

    public synchronized boolean deactivate(String serviceId) throws IOException {
        Service service = findById(serviceId);
        if (service == null) throw new IllegalArgumentException("Service not found: " + serviceId);
        service.setStatus("Inactive");
        return update(service);
    }


    public String generateNextId() throws IOException {
        List<Service> all = findAll();
        int max = 0;
        for (Service s : all) {
            try {
                int num = Integer.parseInt(s.getServiceId().replaceAll("[^0-9]", ""));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("SRV%03d", max + 1);
    }
}
