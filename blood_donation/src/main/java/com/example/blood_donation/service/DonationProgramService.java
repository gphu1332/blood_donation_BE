package com.example.blood_donation.service;

import com.example.blood_donation.dto.DonationProgramDTO;
import com.example.blood_donation.entity.DonationProgram;
import com.example.blood_donation.entity.Location;
import com.example.blood_donation.entity.Slot;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.DonationProgramRepository;
import com.example.blood_donation.repositoty.LocationRepository;
import com.example.blood_donation.repositoty.SlotRepository;
import com.example.blood_donation.repositoty.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DonationProgramService {

    @Autowired
    private DonationProgramRepository donationProgramRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Lấy tất cả chương trình.
     */
    public List<DonationProgramDTO> getAll() {
        return donationProgramRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Lấy chương trình theo ID.
     */
    public DonationProgramDTO getById(Long id) {
        DonationProgram program = donationProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));
        return mapToDTO(program);
    }

    /**
     * Tìm chương trình có date nằm giữa startDate và endDate, theo locationId.
     */
    public List<DonationProgramDTO> searchByDateInRangeAndLocationID(LocalDate date, Long locationId) {
        List<DonationProgram> programs = donationProgramRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndLocation_Id(
                        date, date, locationId
                );
        return programs.stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Tạo mới DonationProgram, tự động gán admin từ người đăng nhập.
     */
    @Transactional
    public DonationProgramDTO create(DonationProgramDTO dto, String adminUsername) {
        DonationProgram program = new DonationProgram();
        program.setProName(dto.getProName());
        program.setStartDate(dto.getStartDate());
        program.setEndDate(dto.getEndDate());
        program.setDateCreated(LocalDate.now());
        program.setAddress(dto.getAddress());

        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("Location not found"));
            program.setLocation(location);
        }

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));
        program.setAdmin(admin);

        if (dto.getSlotIds() != null && !dto.getSlotIds().isEmpty()) {
            List<Slot> slots = slotRepository.findAllById(dto.getSlotIds());
            slots.forEach(slot -> slot.setProgram(program));
            program.setSlots(slots);
        }

        DonationProgram saved = donationProgramRepository.save(program);
        return mapToDTO(saved);
    }



    /**
     * Cập nhật chương trình.
     */
    public DonationProgramDTO update(Long id, DonationProgramDTO dto) {
        DonationProgram existing = donationProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));

        existing.setProName(dto.getProName());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setAddress(dto.getAddress());

        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("Location not found"));
            existing.setLocation(location);
        }

        if (dto.getSlotIds() != null) {
            List<Slot> slots = slotRepository.findAllById(dto.getSlotIds());
            slots.forEach(slot -> slot.setProgram(existing));
            existing.setSlots(slots);
        }

        DonationProgram updated = donationProgramRepository.save(existing);
        return mapToDTO(updated);
    }

    /**
     * Xóa chương trình.
     */
    public void delete(Long id) {
        donationProgramRepository.deleteById(id);
    }

    /**
     * Map Entity -> DTO.
     */
    private DonationProgramDTO mapToDTO(DonationProgram program) {
        DonationProgramDTO dto = new DonationProgramDTO();
        dto.setId(program.getId()); // hoặc setProId(program.getId())
        dto.setProName(program.getProName());
        dto.setStartDate(program.getStartDate());
        dto.setEndDate(program.getEndDate());
        dto.setAddress(program.getAddress());

        if (program.getLocation() != null) {
            dto.setLocationId(program.getLocation().getId());
        }
        if (program.getAdmin() != null) {
            dto.setAdminId(program.getAdmin().getUserID());
        }
        if (program.getSlots() != null) {
            dto.setSlotIds(program.getSlots()
                    .stream()
                    .map(Slot::getSlotID)
                    .toList());
        }
        return dto;
    }

}
