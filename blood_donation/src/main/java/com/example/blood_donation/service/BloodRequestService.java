package com.example.blood_donation.service;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.dto.BloodRequestDetailDTO;
import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.entity.BloodRequestDetail;
import com.example.blood_donation.entity.MedicalStaff;
import com.example.blood_donation.entity.Staff;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.repository.BloodRequestDetailRepository;
import com.example.blood_donation.dto.BloodRequestResponseDTO;
import com.example.blood_donation.repository.BloodRequestRepository;
import jakarta.transaction.Transactional;
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
    @Transactional
    public BloodRequest createRequestFromDTO(BloodRequestDTO dto) {
        BloodRequest req = new BloodRequest();
        req.setReqCreateDate(LocalDate.now());
        req.setIsEmergency(dto.getIsEmergency());
        req.setStatus(Status.PENDING);

        MedicalStaff medicalStaff = new MedicalStaff();
        medicalStaff.setId(dto.getMedId());
        req.setMedicalStaff(medicalStaff);

        List<BloodRequestDetail> details = dto.getDetails().stream().map(d -> {
            BloodRequestDetail detail = new BloodRequestDetail();
            detail.setTypeBlood(d.getTypeBlood());
            detail.setPackVolume(d.getPackVolume());
            detail.setPackCount(d.getPackCount());
            detail.setBloodRequest(req);
            return detail;
        }).toList();
        req.setDetails(details);
        return reqRepo.save(req);
    }

    // Cập nhập yêu cầu - Medical Staff
    @Transactional
    public BloodRequest updateRequestByMedical(Long id, BloodRequestDTO dto) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

        if (!req.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Chỉ được cập nhập khi trạng thái là PENDING");
        }

        req.setIsEmergency(dto.getIsEmergency());
        detailRepo.deleteAll(detailRepo.findByReqID(id));
        List<BloodRequestDetail> details = dto.getDetails().stream().map(d -> {
            BloodRequestDetail detail = new BloodRequestDetail();
            detail.setTypeBlood(d.getTypeBlood());
            detail.setPackVolume(d.getPackVolume());
            detail.setPackCount(d.getPackCount());
            detail.setBloodRequest(req);
            return detail;
        }).toList();
        req.setDetails(details);
        return reqRepo.save(req);
    }
    // Hủy yêu cầu - Medical Staff
    public void cancelRequestByMedical(Long id) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        req.setStatus(Status.CANCELLED);
        reqRepo.save(req);
    }
    public List<BloodRequest> getRequestByMedical(Long medId) {
        return reqRepo.findByMedicalStaff_Id(medId);
    }

    public List<BloodRequestResponseDTO> getRequestDTOByMedical(Long medId) {
        List<BloodRequest> requests = reqRepo.findByMedicalStaff_IdAndIsDeletedFalse(medId);
        return requests.stream().map(this::mapToResponseDTO).toList();
    }
    // Duyệt/ Từ chối yêu cầu - Staff
    public BloodRequest respondToRequest(Long id, String action, Long staffId) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        if("accept".equalsIgnoreCase(action)) {
            req.setStatus(Status.APPROVED);
        } else if ("reject".equalsIgnoreCase(action)) {
            req.setStatus(Status.REJECTED);
        } else {
            throw new IllegalArgumentException("Hành động không hợp lệ");
        }

        Staff staff = new Staff();
        staff.setId(staffId);
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

    public List<BloodRequest> getRequestsByStaff(Long staId) {
        return reqRepo.findByStaff_Id(staId);
    }

    public BloodRequest getById(Long id) {
        return reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu máu"));
    }
    public List<BloodRequest> getAllRequests() {
        return reqRepo.findByIsDeletedFalse();
    }
// Phan Kim code test BE
    public List<BloodRequestResponseDTO> getAllRequestDTOs() {
        return reqRepo.findByIsDeletedFalse()
                .stream().map(this::mapToResponseDTO)
                .toList();
    }
    @Transactional
    public void delete(Long id) {
        BloodRequest request = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        request.setDeleted(true);
        reqRepo.save(request);
    }
    private BloodRequestResponseDTO mapToResponseDTO(BloodRequest req) {
        BloodRequestResponseDTO dto = new BloodRequestResponseDTO();
        dto.setReqID(req.getReqID());
        dto.setIsEmergency(req.getIsEmergency());
        dto.setStatus(req.getStatus().name());
        dto.setReqCreateDate(req.getReqCreateDate());
        List<BloodRequestDetailDTO> detailDTOs = req.getDetails().stream().map(detail -> {
            BloodRequestDetailDTO d = new BloodRequestDetailDTO();
            d.setTypeBlood(detail.getTypeBlood());
            d.setPackCount(detail.getPackCount());
            d.setPackVolume(detail.getPackVolume());
            return d;
        }).toList();
        dto.setDetails(detailDTOs);
        return dto;
    }
}