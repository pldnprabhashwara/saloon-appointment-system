package com.example.demo.service;

import com.example.demo.model.Service;
import com.example.demo.repository.ServiceRepository;
import java.io.IOException;
import java.util.List;

public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService() {
        this.serviceRepository = ServiceRepository.getInstance();
    }

    public boolean addService(String serviceName, String category,
                              double price, int durationMinutes, String description) throws Exception {
        // Validate
        if (serviceName == null || serviceName.trim().isEmpty())
            throw new IllegalArgumentException("Service name cannot be empty");
        if (category == null || category.trim().isEmpty())
            throw new IllegalArgumentException("Category cannot be empty");
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be negative");
        if (durationMinutes < 5)
            throw new IllegalArgumentException("Duration must be at least 5 minutes");

        // Auto-generate ID
        String serviceId = serviceRepository.generateNextId();

        Service service = new Service(serviceId, serviceName.trim(), category.trim(),
                                      price, durationMinutes,
                                      description != null ? description.trim() : "");
        return serviceRepository.save(service);
    }


    public List<Service> getAllServices() throws IOException {
        return serviceRepository.findAll();
    }


    public List<Service> getActiveServices() throws IOException {
        return serviceRepository.findAllActive();
    }


    public Service getServiceById(String serviceId) throws IOException {
        if (serviceId == null || serviceId.trim().isEmpty())
            throw new IllegalArgumentException("Service ID cannot be empty");
        Service s = serviceRepository.findById(serviceId);
        if (s == null) throw new IllegalArgumentException("Service not found: " + serviceId);
        return s;
    }


    public List<Service> searchServices(String keyword) throws IOException {
        if (keyword == null || keyword.trim().isEmpty()) return getAllServices();
        return serviceRepository.searchByName(keyword.trim());
    }

    public List<Service> getServicesByCategory(String category) throws IOException {
        if (category == null || category.trim().isEmpty()) return getAllServices();
        return serviceRepository.findByCategory(category.trim());
    }

    public boolean updateService(String serviceId, String serviceName, String category,
                                  double price, int durationMinutes, String description) throws Exception {
        Service service = serviceRepository.findById(serviceId);
        if (service == null) throw new IllegalArgumentException("Service not found: " + serviceId);

        if (serviceName != null && !serviceName.trim().isEmpty())
            service.setServiceName(serviceName.trim());
        if (category != null && !category.trim().isEmpty())
            service.setCategory(category.trim());
        if (price >= 0)
            service.setPrice(price);
        if (durationMinutes >= 5)
            service.setDurationMinutes(durationMinutes);
        if (description != null)
            service.setDescription(description.trim());

        return serviceRepository.update(service);
    }

    public boolean deleteService(String serviceId) throws IOException {
        if (serviceId == null || serviceId.trim().isEmpty())
            throw new IllegalArgumentException("Service ID cannot be empty");
        return serviceRepository.delete(serviceId);
    }


    public boolean deactivateService(String serviceId) throws IOException {
        return serviceRepository.deactivate(serviceId);
    }

    // ==================== VALIDATION HELPERS ====================

    public static boolean isValidPrice(double price) {
        return price >= 0;
    }

    public static boolean isValidDuration(int minutes) {
        return minutes >= 5 && minutes <= 480;
    }
}
