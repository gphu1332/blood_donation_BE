package com.example.blood_donation.service;

import com.example.blood_donation.dto.BloodUnitResponseDTO;
import com.example.blood_donation.dto.BloodUnitSearchDTO;
import com.example.blood_donation.dto.CreateBloodUnitDTO;
import com.example.blood_donation.dto.UpdateBloodUnitDTO;
import com.example.blood_donation.entity.BloodUnit;
import com.example.blood_donation.enums.TypeBlood;
import com.example.blood_donation.repository.BloodRequestRepository;
import com.example.blood_donation.repository.BloodUnitRepository;
import com.example.blood_donation.repository.DonationDetailRepository;
import com.example.blood_donation.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Service
public class BloodUnitService {
    @Autowired
    private BloodUnitRepository repository;

    @Autowired
    private DonationDetailRepository donationDetailRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    private String generateSerialCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = repository.count();
        String serialNumber = String.format("%04d", count + 1);
        return "BLD-" + datePart + "-" + serialNumber;
    }
    public BloodUnitResponseDTO toResponseDTO(BloodUnit unit) {
        BloodUnitResponseDTO dto = new BloodUnitResponseDTO();
        dto.setId(unit.getBloodUnitID());
        dto.setVolume(unit.getVolume());
        dto.setDateImport(unit.getDateImport());
        dto.setExpiryDate(unit.getExpiryDate());
        dto.setBloodSerialCode(unit.getBloodSerialCode());
        dto.setTypeBlood(unit.getTypeBlood());
        dto.setDonationDetailId(unit.getDonationDetail() != null ? unit.getDonationDetail().getDonID() : null);
        dto.setStaffId(unit.getStaff().getId());
        dto.setBloodRequestId(
                unit.getRequest() != null ? unit.getRequest().getReqID() : null);
        return dto;

    }

    public BloodUnit create(CreateBloodUnitDTO dto) {
       BloodUnit unit = new BloodUnit();
       unit.setVolume(dto.getVolume());
       unit.setDateImport(dto.getDateImport());
       unit.setExpiryDate(dto.getExpiryDate());
       unit.setBloodSerialCode(dto.getBloodSerialCode());
       unit.setTypeBlood(dto.getTypeBlood());

        // ✅ Cho phép để trống donationDetailId
        if (dto.getDonationDetailId() != null) {
            unit.setDonationDetail(donationDetailRepository.findById(dto.getDonationDetailId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy DonationDetail")));
        } else {
            unit.setDonationDetail(null);
        }

       unit.setStaff(staffRepository.findById(dto.getStaffId())
               .orElseThrow(() -> new RuntimeException("Không tìm thấy Staff")));

        // ✅ Cho phép để trống bloodRequestId
        if (dto.getBloodRequestId() != null) {
            unit.setRequest(bloodRequestRepository.findById(dto.getBloodRequestId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy BloodRequest")));
        } else {
            unit.setRequest(null);
        }

       return repository.save(unit);
    }
    public List<BloodUnit> getAll() {
        return repository.findAll();
    }
    public BloodUnit getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy túi máu có ID: " + id));
    }

    public BloodUnit update(Long id, UpdateBloodUnitDTO dto) {
        BloodUnit existing = getById(id);

        existing.setBloodSerialCode(dto.getBloodSerialCode());
        existing.setVolume(dto.getVolume());
        existing.setDateImport(dto.getDateImport());
        existing.setExpiryDate(dto.getExpiryDate());
        existing.setTypeBlood(dto.getTypeBlood());

        // ✅ Cho phép DonationDetailId null
        if (dto.getDonationDetailId() != null) {
            existing.setDonationDetail(donationDetailRepository.findById(dto.getDonationDetailId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy DonationDetail")));
        } else {
            existing.setDonationDetail(null);
        }

        // ✅ Staff vẫn bắt buộc
        existing.setStaff(staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Staff")));

        // ✅ Cho phép BloodRequestId null
        if (dto.getBloodRequestId() != null) {
            existing.setRequest(bloodRequestRepository.findById(dto.getBloodRequestId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy BloodRequest")));
        } else {
            existing.setRequest(null);
        }

        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    //Kim
    public List<BloodUnit> searchBloodUnits(BloodUnitSearchDTO dto) {
        String code = dto.getBloodSerialCode();
        TypeBlood type = dto.getTypeBlood();

        if (code != null && !code.isEmpty() && type != null) {
            return repository.findByTypeBloodAndBloodSerialCodeContainingIgnoreCase(type, code);
        }
        if (code != null && !code.isEmpty()) {
            return repository.findByBloodSerialCodeContainingIgnoreCase(code);
        }
        if (type != null) {
            return repository.findByTypeBlood(type);
        }
        return repository.findAll();
    }

    public List<BloodUnit> getUnitsByRequestId(Long requestId) {
        return repository.findByRequest_ReqID(requestId);
    }

}
