package com.example.blood_donation.service;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.dto.BloodRequestDetailDTO;
import com.example.blood_donation.dto.BloodRequestResponseDTO;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BloodRequestService {

    @Autowired
    private BloodRequestRepository reqRepo;

    @Autowired
    private BloodRequestDetailRepository detailRepo;

    @Autowired
    private MedicalStaffRepository medicalRepo;

    @Autowired
    private UserRepository userRepository;

    // ✅ 1. Tạo yêu cầu
    @Transactional
    public BloodRequest createRequestFromDTO(BloodRequestDTO dto) {
        BloodRequest req = new BloodRequest();
        req.setReqCreateDate(LocalDate.now());
        req.setIsEmergency(dto.getIsEmergency());
        req.setStatus(Status.PENDING);
        req.setIsDeleted(false);

        MedicalStaff medicalUser = medicalRepo.findById(dto.getMedId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        if (medicalUser.getRole() != Role.HOSPITAL_STAFF) {
            throw new RuntimeException("Người dùng không phải là Medical Staff");
        }

        req.setMedicalStaff(medicalUser);

        // 👇 Build detail list before saving request
        List<BloodRequestDetail> details = dto.getDetails().stream().map(d -> {
            BloodRequestDetailId id = new BloodRequestDetailId(null, d.getTypeBlood()); // null for now
            BloodRequestDetail detail = new BloodRequestDetail();
            detail.setPackCount(d.getPackCount());
            detail.setPackVolume(d.getPackVolume());
            detail.setBloodRequest(req); // 👈 will be populated when req is saved
            detail.setId(id); // req ID will be updated by Hibernate automatically
            return detail;
        }).toList();

        req.setDetails(details); // ✅ attach details before save

        return reqRepo.save(req); // ✅ single save handles everything
    }



    // ✅ 2. Cập nhật yêu cầu
    @Transactional
    public BloodRequest updateRequestByMedical(Long id, BloodRequestDTO dto) {
        try {
            BloodRequest req = reqRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

            if (!req.getStatus().equals(Status.PENDING)) {
                throw new IllegalStateException("Chỉ được cập nhật khi trạng thái là PENDING");
            }

            req.setIsEmergency(dto.getIsEmergency());

            // ✅ 1. Xoá phần tử cũ khỏi collection hiện tại
            req.getDetails().clear(); // Hibernate tự xoá vì orphanRemoval = true
            System.out.println("✅ Đã xoá detail cũ bằng clear()");

            // ✅ 2. Thêm phần tử mới vào collection đang được theo dõi
            for (BloodRequestDetailDTO d : dto.getDetails()) {
                BloodRequestDetail detail = new BloodRequestDetail();
                detail.setId(new BloodRequestDetailId(id, d.getTypeBlood()));
                detail.setPackCount(d.getPackCount());
                detail.setPackVolume(d.getPackVolume());
                detail.setBloodRequest(req); // THAM CHIẾU ĐÚNG mappedBy
                req.getDetails().add(detail); // THÊM TRỰC TIẾP
            }

            // ✅ 3. Lưu BloodRequest, không cần saveAll chi tiết
            return reqRepo.save(req);

        } catch (Exception e) {
            System.out.println("❌ Lỗi updateRequestByMedical: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
    
    // ✅ 3. Hủy yêu cầu
    public void cancelRequestByMedical(Long id) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        req.setStatus(Status.CANCELLED);
        reqRepo.save(req);
    }

    // ✅ 4. Lấy danh sách theo Medical
    public List<BloodRequestResponseDTO> getRequestDTOByMedical(Long medId) {
        List<BloodRequest> requests = reqRepo.findByMedicalStaff_IdAndIsDeletedFalse(medId);
        return requests.stream().map(this::mapToResponseDTO).toList();
    }

    // ✅ 5. Duyệt / Từ chối
    public BloodRequest respondToRequest(Long id, String action, Long staffId) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        if (staff.getRole() != Role.STAFF) {
            throw new RuntimeException("Chỉ STAFF mới được duyệt yêu cầu");
        }

        if (req.getStatus() != Status.PENDING) {
            throw new RuntimeException("Yêu cầu đã được xử lý bởi người khác.");
        }

        if (action.equalsIgnoreCase("accept")) {
            req.setStatus(Status.APPROVED);
        } else if (action.equalsIgnoreCase("reject")) {
            req.setStatus(Status.REJECTED);
        } else {
            throw new IllegalArgumentException("Hành động không hợp lệ");
        }

        req.setHandledBy(staff);
        return reqRepo.save(req);
    }

    // ✅ 6. Cập nhật trạng thái xử lý
    public BloodRequest updateProcessingStatus(Long id, Status status) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        req.setStatus(status);
        return reqRepo.save(req);
    }

    // ✅ 7. Lấy theo staff xử lý
    public List<BloodRequest> getRequestsByStaff(Long staffId) {
        return reqRepo.findByHandledBy_IdAndIsDeletedFalse(staffId);
    }

    // ✅ 8. Lấy tất cả
    public List<BloodRequestResponseDTO> getAllRequestDTOs() {
        return reqRepo.findAll().stream()
                .filter(r -> !r.getIsDeleted())
                .map(this::mapToResponseDTO)
                .toList();
    }

    // ✅ 9. Xóa mềm
    public void delete(Long id) {
        BloodRequest req = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        req.setIsDeleted(true);
        reqRepo.save(req);
    }

    // ✅ 10. Map sang DTO
    public BloodRequestResponseDTO mapToResponseDTO(BloodRequest req) {
        BloodRequestResponseDTO dto = new BloodRequestResponseDTO();
        dto.setReqID(req.getReqID());
        dto.setIsEmergency(req.getIsEmergency());
        dto.setStatus(req.getStatus().name());
        dto.setReqCreateDate(req.getReqCreateDate());

        dto.setMedId(req.getMedicalStaff() != null ? req.getMedicalStaff().getId() : null);

        List<BloodRequestDetailDTO> detailDTOs = req.getDetails().stream().map(d -> {
            BloodRequestDetailDTO dtoDetail = new BloodRequestDetailDTO();
            dtoDetail.setTypeBlood(d.getId().getTypeBlood());
            dtoDetail.setPackCount(d.getPackCount());
            dtoDetail.setPackVolume(d.getPackVolume());
            return dtoDetail;
        }).toList();

        dto.setDetails(detailDTOs);

        if (req.getHandledBy() != null) {
            dto.setHandledById(req.getHandledBy().getId());
            dto.setHandledByName(req.getHandledBy().getFullName());
            dto.setHandledByEmail(req.getHandledBy().getEmail());
        }

        return dto;
    }

    // Tìm thông tin blood request theo ID:
    public BloodRequestResponseDTO getRequestDTOById(Long id) {
        BloodRequest request = reqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        return mapToResponseDTO(request);
    }
}
