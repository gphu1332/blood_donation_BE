package com.example.blood_donation.service;

import com.example.blood_donation.entity.BloodUnit;
import com.example.blood_donation.repositoty.BloodUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BloodUnitService {
    @Autowired
    private BloodUnitRepository repository;

    private String generateSerialCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = repository.count();
        String serialNumber = String.format("%04d", count + 1);
        return "BLD-" + datePart + "-" + serialNumber;
    }
    public BloodUnit create(BloodUnit unit) {
        unit.setBloodSerialCode(generateSerialCode());
        return repository.save(unit);
    }
    public List<BloodUnit> getAll() {
        return repository.findAll();
    }
    public BloodUnit getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy túi máu có ID: " + id));
    }
    public BloodUnit update(Long id, BloodUnit updated) {
        BloodUnit existing = getById(id);
        existing.setBloodSerialCode(updated.getBloodSerialCode());
        existing.setVolume(updated.getVolume());
        existing.setDateImport(updated.getDateImport());
        existing.setExpiryDate(updated.getExpiryDate());
        existing.setBloodType(updated.getBloodType());
        existing.setDonationDetail(updated.getDonationDetail());
        existing.setStaff(updated.getStaff());
        existing.setRequest(updated.getRequest());
        return repository.save(existing);
    }
}
