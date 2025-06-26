package com.example.blood_donation.service;

import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.repositoty.BloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloodRequestService {
    @Autowired
    private BloodRequestRepository bloodRequestRepository;
    public List<BloodRequest> getAll() {
        return bloodRequestRepository.findAll();
    }
    public BloodRequest getById(Long id) {
        return bloodRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu máu"));
    }
    public BloodRequest create(BloodRequest request) {
        return bloodRequestRepository.save(request);
    }
    public BloodRequest update(Long id, BloodRequest req) {
        BloodRequest existing = getById(id);
        existing.setReqCreateDate(req.getReqCreateDate());
        existing.setIsEmergency(req.getIsEmergency());
        existing.setReqStatus(req.getReqStatus());
        existing.setStaff(req.getStaff());
        existing.setMedicalStaff(req.getMedicalStaff());
        return bloodRequestRepository.save(existing);
    }
    public void delete(Long id) {
        bloodRequestRepository.deleteById(id);
    }


}
