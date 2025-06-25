package com.example.blood_donation.service;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.dto.BloodRequestDetailDTO;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.repositoty.BloodRequestDetailRepository;
import com.example.blood_donation.repositoty.BloodRequestRepository;
import com.example.blood_donation.repositoty.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BloodRequestService {
    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private BloodRequestDetailRepository detailRepo;

    @Autowired
    private UserRepository userRepository;

    //1. Xem các yêu cầu đang chờ xử lý
    public List<BloodRequest> getAllPending() {
        return bloodRequestRepository.findByReqStatusIn(List.of(Status.PENDING));
    }

    //2. Cập nhập trạng thái yêu cầu
    public void updateStatus(Long reqID, Status status) {
        BloodRequest request = bloodRequestRepository.findById(reqID)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setReqStatus(status);
        bloodRequestRepository.save(request);
    }

    //3. Gán BloodStaff vào yêu cầu máu
    public void assignStaff(Long reqID, Long staffID) {
        BloodRequest request = bloodRequestRepository.findById(reqID)
                .orElseThrow(() -> new RuntimeException("Rerquest not found"));
        User user = userRepository.findById(staffID)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        if(!(user instanceof Staff)) {
            throw new RuntimeException("User is not a Staff");
        }
        request.setStaff((Staff) user);
        request.setReqStatus(Status.PENDING);
        bloodRequestRepository.save(request);
    }

    public List<BloodRequest> getAll() {
        return bloodRequestRepository.findAll();
    }

    public BloodRequest getById(Long id) {
        return bloodRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu máu"));
    }

    public BloodRequest create(BloodRequestDTO request) {
        // Lấy MedicalStaff từ User
        User user = userRepository.findById(request.getMedID())
                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));
        if(!(user instanceof MedicalStaff)) {
            throw new IllegalArgumentException("User is not a medical satff");
        }
        MedicalStaff medicalStaff = (MedicalStaff) user;
        // Tạo yêu cầu máu
        BloodRequest bloodRequest = new BloodRequest();
        bloodRequest.setReqCreateDate(request.getReqCreatedDate());
        bloodRequest.setIsEmergency(request.getIsEmergency());
        bloodRequest.setReqStatus(request.getReqStatus());
        bloodRequest.setMedicalStaff(medicalStaff);
        //Gán BloodStaff nếu có
        if(request.getStaID()!=null) {
            User staffUser = userRepository.findById(request.getStaID())
                    .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
            if (!(staffUser instanceof Staff)) {
                throw new IllegalArgumentException("User is not a staff");
            }
            bloodRequest.setStaff((Staff) staffUser);
        }
        //Chi tiết yêu cầu
        List<BloodRequestDetail> details = new ArrayList<>();
        for (BloodRequestDetailDTO dto : request.getDetails()) {
            BloodRequestDetailId id = new BloodRequestDetailId(null, dto.getBloodType());
            BloodRequestDetail detail = new BloodRequestDetail();
            detail.setId(id);
            detail.setPackCount(dto.getPackCount());
            detail.setPackVolume(dto.getPackVolume());
            detail.setBloodRequest(bloodRequest);
            details.add(detail);
        }
        bloodRequest.setDetails(details);
        return bloodRequestRepository.save(bloodRequest);
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
