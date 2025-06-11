package com.example.blood_donation.service;

import com.example.blood_donation.entity.BloodInventory;
import com.example.blood_donation.repositoty.BloodInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BloodInventoryService {
    @Autowired
    private BloodInventoryRepository repository;

    public List<BloodInventory> getAll() {
        return repository.findAll();
    }

    public Optional<BloodInventory> getById(Integer id) {
        return repository.findById(id);
    }

    public BloodInventory save(BloodInventory bloodInventory) {
        return repository.save(bloodInventory);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
