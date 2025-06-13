package com.example.blood_donation.service;

import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.entity.BloodRequestPriority;
import com.example.blood_donation.enums.RequestStatus;
import com.example.blood_donation.repositoty.BloodRequestPriorityRepository;
import com.example.blood_donation.repositoty.BloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BloodRequestService {
    @Autowired
    private BloodRequestRepository requestRepository;
    @Autowired
    private BloodRequestPriorityRepository priorityRepository;
    public BloodRequest create(BloodRequest request, List<BloodRequestPriority> priorities) {
        request.setResDateCreated(LocalDate.now());
        request.setResStatus(RequestStatus.PENDING);
        BloodRequest saved = requestRepository.save(request);
        for(BloodRequestPriority priority : priorities) {
            priority.setBloodRequest(saved);
            priorityRepository.save(priority);
        }
        return saved;
    }
    public List<BloodRequest> getAll() {
        return requestRepository.findAll();
    }
    public void delete(Integer id) {
        requestRepository.deleteById(id);
    }
    public BloodRequest updateStatus(Integer id, RequestStatus status) {
        BloodRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new  RuntimeException("Not found"));
        request.setResStatus(status);
        return requestRepository.save(request);
    }
    public List<BloodRequestPriority> getPriorityList(Integer resId) {
        return priorityRepository.findByBloodRequest_ResIdOrderByPriorityOrderAsc(resId);
    }
}
