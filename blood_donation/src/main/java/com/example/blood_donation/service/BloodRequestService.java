package com.example.blood_donation.service;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.dto.BloodRequestDetailDTO;
import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.entity.BloodRequestDetail;
import com.example.blood_donation.entity.MedicalStaff;
import com.example.blood_donation.entity.Staff;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.repositoty.BloodRequestDetailRepository;
import com.example.blood_donation.repositoty.BloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BloodRequestService {
    @Autowired
    private BloodRequestRepository reqRepo;
    @Autowired
    private BloodRequestDetailRepository detailRepo;

    // Tạo yêu cầu mới - Medical Staff
    public BloodRequest createRequest(BloodRequestDTO dto) {
        BloodRequest req = new BloodRequest();
        req.setReqCreateDate(LocalDate.now());
        req.setIsEmergency(dto.getIsEmergency());
        req.setReqStatus(Status.PENDING);

        MedicalStaff medicalStaff = new MedicalStaff();
        medicalStaff.setUserID(dto.getMedId());
        req.setMedicalStaff(medicalStaff);

        BloodRequest saved = reqRepo.save(req);

        for(BloodRequestDetailDTO d : dto.getDetails()) {
            BloodRequestDetail detail = new BloodRequestDetail();
            detail.setReqID(saved.getReqID());
            detail.setBloodType(d.getBloodType());
            detail.setPackVolume(d.getPackVolume());
            detail.setPackCount(d.getPackCount());
            detailRepo.save(detail);
        }
        return saved;
    }

    // Cập nhập yêu cầu - Medical Staff
    public BloodRequest updateRequest(Long id, BloodRequestDTO dto) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

        if(!req.getReqStatus().equals("PENDING")) {
            throw new IllegalStateException("Chỉ được cập nhập khi trạng thái là PENDING");
        }

        req.setIsEmergency(dto.getIsEmergency());
        reqRepo.save(req);

        detailRepo.deleteAll(detailRepo.findByReqID(id));
        for (BloodRequestDetailDTO d : dto.getDetails()) {
            BloodRequestDetail detail = new BloodRequestDetail();
            detail.setReqID(req.getReqID());
            detail.setBloodType(d.getBloodType());
            detail.setPackVolume(d.getPackVolume());
            detail.setPackCount(d.getPackCount());
            detailRepo.save(detail);
        }

        return req;
    }

    // Hủy yêu cầu - Medical Staff
    public void cancelRequest(Long id) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        req.setReqStatus(Status.CANCELLED);
        reqRepo.save(req);
    }

    // Duyệt/ Từ chối yêu cầu - Staff
    public BloodRequest respondToRequest(Long id, String action, Long staffId) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        if(action.equalsIgnoreCase("accept")) {
            req.setReqStatus(Status.APPROVED);
        } else if (action.equalsIgnoreCase("reject")) {
            req.setReqStatus(Status.REJECTED);
        } else {
            throw new IllegalArgumentException("Hành động không hợp lệ");
        }

        Staff staff = new Staff();
        staff.setUserID(staffId);
        req.setStaff(staff);

        return reqRepo.save(req);
    }

    // Cập nhập trạng thái xử lý - Staff
    public BloodRequest updateProcessingStatus(Long id, Status newStatus) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        req.setReqStatus(newStatus);
        return reqRepo.save(req);
    }

    public List<BloodRequest> getRequestByMedical(Long medId) {
        return reqRepo.findByMedicalStaff_UserID(medId);
    }

    public List<BloodRequest> getRequestsByStaff(Long staId) {
        return reqRepo.findByStaff_UserID(staId);
    }

    public List<BloodRequest> getAllRequests() {
        return reqRepo.findAll();
    }
}