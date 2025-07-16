package com.example.blood_donation.service;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.dto.BloodRequestDetailDTO;
import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.entity.BloodRequestDetail;
import com.example.blood_donation.entity.MedicalStaff;
import com.example.blood_donation.entity.Staff;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.repositoty.BloodRequestDetailRepository;
import com.example.blood_donation.dto.BloodRequestResponseDTO;
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
        req.setStatus(Status.PENDING);

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

        if (req.getStatus() != Status.PENDING) {
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
        existing.setStatus(req.getStatus());
        existing.setStaff(req.getStaff());
        existing.setMedicalStaff(req.getMedicalStaff());
        return bloodRequestRepository.save(existing);
    }
    public void delete(Long id) {
        bloodRequestRepository.deleteById(id);
    }

    // Hủy yêu cầu - Medical Staff
    public void cancelRequest(Long id) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        req.setStatus(Status.CANCELLED);
        reqRepo.save(req);
    }

    // Duyệt/ Từ chối yêu cầu - Staff
    public BloodRequest respondToRequest(Long id, String action, Long staffId) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        if(action.equalsIgnoreCase("accept")) {
            req.setStatus(Status.APPROVED);
        } else if (action.equalsIgnoreCase("reject")) {
            req.setStatus(Status.REJECTED);
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
        req.setStatus(newStatus);
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
// Phan Kim code test BE
    public List<BloodRequestResponseDTO> getAllRequestDTOs() {
        List<BloodRequest> all = reqRepo.findAll();

        return all.stream().map(req -> {
            BloodRequestResponseDTO dto = new BloodRequestResponseDTO();
            dto.setReqID(req.getReqID());
            dto.setIsEmergency(req.getIsEmergency());
            dto.setStatus(req.getStatus().toString());
            dto.setReqCreateDate(req.getReqCreateDate());

            List<BloodRequestDetailDTO> detailDTOs = req.getDetails().stream().map(detail -> {
                BloodRequestDetailDTO d = new BloodRequestDetailDTO();
                d.setBloodType(detail.getBloodType());
                d.setPackCount(detail.getPackCount());
                d.setPackVolume(detail.getPackVolume());
                return d;
            }).toList();

            dto.setDetails(detailDTOs);
            return dto;
        }).toList();
    }

    public List<BloodRequestResponseDTO> getRequestsByMedicalDTO(Long medId) {
        List<BloodRequest> requests = reqRepo.findByMedicalStaff_UserID(medId);
        return requests.stream().map(req -> {
            BloodRequestResponseDTO dto = new BloodRequestResponseDTO();
            dto.setReqID(req.getReqID());
            dto.setIsEmergency(req.getIsEmergency());
            dto.setStatus(req.getStatus().name());
            dto.setReqCreateDate(req.getReqCreateDate());

            List<BloodRequestDetailDTO> detailDTOs = req.getDetails().stream().map(d -> {
                BloodRequestDetailDTO dDTO = new BloodRequestDetailDTO();
                dDTO.setBloodType(d.getBloodType());
                dDTO.setPackVolume(d.getPackVolume());
                dDTO.setPackCount(d.getPackCount());
                return dDTO;
            }).toList();

            dto.setDetails(detailDTOs);
            return dto;
        }).toList();
    }

    public void deleteRequest(Long id) {
        BloodRequest request = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

        // Delete all details first
        detailRepo.deleteAll(detailRepo.findByReqID(id));

        // Then delete the request
        reqRepo.deleteById(id);
    }



}