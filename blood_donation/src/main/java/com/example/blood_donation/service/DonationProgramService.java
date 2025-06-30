package com.example.blood_donation.service;

import com.example.blood_donation.dto.DonationProgramDTO;
import com.example.blood_donation.entity.DonationProgram;
import com.example.blood_donation.entity.Location;
import com.example.blood_donation.entity.Slot;
import com.example.blood_donation.repositoty.DonationProgramRepository;
import com.example.blood_donation.repositoty.LocationRepository;
import com.example.blood_donation.repositoty.SlotRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationProgramService {

    @Autowired
    private DonationProgramRepository donationProgramRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private LocationRepository locationRepository;

    public List<DonationProgramDTO> getAll() {
        return donationProgramRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DonationProgramDTO getById(Long id) {
        DonationProgram program = donationProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));
        return mapToDTO(program);
    }

    public List<DonationProgramDTO> searchByDateAndLocation(LocalDate date, String location) {
        List<DonationProgram> programs = donationProgramRepository
                .findByStartDateAndLocation_Name(date, location);
        return programs.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DonationProgramDTO create(DonationProgramDTO dto) {
        DonationProgram program = new DonationProgram();
        program.setProName(dto.getProName());
        program.setDateCreated(LocalDate.now());
        program.setEndDate(dto.getEndDate());
        program.setStartDate(dto.getStartDate());

        // Gán location
        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("Location not found"));
            program.setLocation(location);
        }

        // Gán slots
        if (dto.getSlotIds() != null && !dto.getSlotIds().isEmpty()) {
            List<Slot> slots = slotRepository.findAllById(dto.getSlotIds());
            slots.forEach(slot -> slot.setProgram(program)); // gán ngược lại để giữ quan hệ bidirectional
            program.setSlots(slots);
        }

        DonationProgram saved = donationProgramRepository.save(program);
        return mapToDTO(saved);
    }

    public DonationProgramDTO update(Long id, DonationProgramDTO dto) {
        DonationProgram existing = donationProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));

        existing.setProName(dto.getProName());

        // Cập nhật location
        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("Location not found"));
            existing.setLocation(location);
        }

        // Cập nhật slots
        if (dto.getSlotIds() != null) {
            List<Slot> slots = slotRepository.findAllById(dto.getSlotIds());
            slots.forEach(slot -> slot.setProgram(existing));
            existing.setSlots(slots);
        }

        DonationProgram updated = donationProgramRepository.save(existing);
        return mapToDTO(updated);
    }

    public void delete(Long id) {
        donationProgramRepository.deleteById(id);
    }

    // ✨ Hàm ánh xạ từ Entity → DTO
    private DonationProgramDTO mapToDTO(DonationProgram program) {
        DonationProgramDTO dto = new DonationProgramDTO();
        dto.setId(program.getId());
        dto.setProName(program.getProName());
        dto.setEndDate(program.getEndDate());
        dto.setStartDate(program.getStartDate());

        if (program.getLocation() != null) {
            dto.setLocationId(program.getLocation().getId());
        }

        if (program.getSlots() != null) {
            dto.setSlotIds(
                    program.getSlots().stream()
                            .map(Slot::getSlotID)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}
