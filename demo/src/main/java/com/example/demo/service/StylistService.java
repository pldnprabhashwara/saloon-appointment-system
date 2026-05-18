package com.example.demo.service;

import com.example.demo.model.Stylist;
import com.example.demo.repository.StylistRepository;
import java.io.IOException;
import java.util.List;

/**
 * Business logic layer for the Stylist module.
 */
public class StylistService {
    private final StylistRepository stylistRepository;

    public StylistService() {
        this.stylistRepository = StylistRepository.getInstance();
    }

    public boolean addStylist(String stylistId, String stylistName, String specialization, boolean availability) throws IOException {
        validateStylistData(stylistId, stylistName, specialization);
        Stylist stylist = new Stylist(stylistId.trim(), stylistName.trim(), specialization.trim(), availability);
        return stylistRepository.save(stylist);
    }

    public List<Stylist> getAllStylists() throws IOException {
        return stylistRepository.findAll();
    }

    public Stylist getStylistById(String stylistId) throws IOException {
        Stylist stylist = stylistRepository.findById(stylistId);
        if (stylist == null) {
            throw new IllegalArgumentException("Stylist not found");
        }
        return stylist;
    }

    public boolean updateStylist(String stylistId, String stylistName, String specialization, boolean availability) throws IOException {
        validateStylistData(stylistId, stylistName, specialization);
        Stylist stylist = stylistRepository.findById(stylistId);
        if (stylist == null) {
            throw new IllegalArgumentException("Stylist not found");
        }
        stylist.setStylistName(stylistName);
        stylist.setSpecialization(specialization);
        stylist.setAvailability(availability);
        return stylistRepository.update(stylist);
    }

    public boolean updateAvailability(String stylistId, boolean availability) throws IOException {
        Stylist stylist = getStylistById(stylistId);
        stylist.setAvailability(availability);
        return stylistRepository.update(stylist);
    }

    public boolean deleteStylist(String stylistId) throws IOException {
        if (stylistId == null || stylistId.trim().isEmpty()) {
            throw new IllegalArgumentException("Stylist ID cannot be empty");
        }
        return stylistRepository.delete(stylistId);
    }

    private void validateStylistData(String stylistId, String stylistName, String specialization) {
        if (stylistId == null || stylistId.trim().isEmpty()) {
            throw new IllegalArgumentException("Stylist ID cannot be empty");
        }
        if (stylistName == null || stylistName.trim().isEmpty()) {
            throw new IllegalArgumentException("Stylist name cannot be empty");
        }
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization cannot be empty");
        }
    }
}
