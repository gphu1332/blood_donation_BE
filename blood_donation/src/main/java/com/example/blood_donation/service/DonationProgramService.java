package com.example.blood_donation.service;

import com.example.blood_donation.dto.DonationProgramDTO;
import com.example.blood_donation.dto.DonationProgramResponse;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.repositoty.*;
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
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdressRepository adressRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Lấy danh sách tất cả chương trình hiến máu.
     */
    public List<DonationProgramResponse> getAll() {
        return donationProgramRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Lấy thông tin chương trình theo ID.
     */
    public DonationProgramResponse getById(Long id) {
        DonationProgram program = donationProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));
        return mapToResponseDTO(program);
    }

    /**
     * Tìm kiếm các chương trình có ngày diễn ra nằm trong khoảng startDate - endDate
     * và thuộc City chỉ định.
     */
    public List<DonationProgramResponse> searchByDateInRangeAndCityID(LocalDate date, Long cityId) {
        List<DonationProgram> programs = donationProgramRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCity_Id(date, date, cityId);
        return programs.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Tạo mới một chương trình hiến máu và gán người tạo là Admin hiện tại.
     */
    @Transactional
    public DonationProgramResponse create(DonationProgramDTO dto, String adminUsername) {
        // 1. Validate trùng chương trình tại địa chỉ
        if (dto.getAddressId() != null) {
            List<DonationProgram> conflicts = donationProgramRepository
                    .findConflictingPrograms(dto.getAddressId(), dto.getStartDate(), dto.getEndDate());

            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Đã có chương trình tại địa chỉ này trong khoảng thời gian đã chọn.");
            }
        }

        DonationProgram program = new DonationProgram();
        program.setProName(dto.getProName());
        program.setStartDate(dto.getStartDate());
        program.setEndDate(dto.getEndDate());
        program.setDateCreated(LocalDate.now());
        program.setTypeBloods(dto.getTypeBloods());
        program.setDescription(dto.getDescription());
        program.setContact(dto.getContact());
        program.setImageUrl(dto.getImageUrl());

        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("City not found"));
            program.setCity(city);
        }

        if (dto.getAddressId() != null) {
            Adress address = adressRepository.findById(dto.getAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Address not found"));
            program.setAddress(address);
        }

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));
        program.setAdmin(admin);

        if (dto.getSlotIds() != null && !dto.getSlotIds().isEmpty()) {
            List<Slot> slots = slotRepository.findAllById(dto.getSlotIds());
            program.setSlots(slots);
        }

        DonationProgram saved = donationProgramRepository.save(program);
        return mapToResponseDTO(saved);
    }

    /**
     * Cập nhật thông tin chương trình đã có.
     */
    @Transactional
    public DonationProgramResponse update(Long id, DonationProgramDTO dto) {
        DonationProgram existing = donationProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));

        // Validate trùng chương trình tại địa chỉ (loại trừ chính mình)
        if (dto.getAddressId() != null) {
            List<DonationProgram> conflicts = donationProgramRepository
                    .findConflictingProgramsExcludingSelf(dto.getAddressId(), dto.getStartDate(), dto.getEndDate(), id);

            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Đã có chương trình tại địa chỉ này trong khoảng thời gian đã chọn.");
            }
        }

        existing.setProName(dto.getProName());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setTypeBloods(dto.getTypeBloods());
        existing.setDescription(dto.getDescription());
        existing.setContact(dto.getContact());
        existing.setImageUrl(dto.getImageUrl());

        if (dto.getAddressId() != null) {
            Adress address = adressRepository.findById(dto.getAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Address not found"));
            existing.setAddress(address);
        }

        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("City not found"));
            existing.setCity(city);
        }

        if (dto.getSlotIds() != null) {
            List<Slot> updatedSlots = slotRepository.findAllById(dto.getSlotIds());
            existing.setSlots(updatedSlots);
        }

        DonationProgram updated = donationProgramRepository.save(existing);
        return mapToResponseDTO(updated);
    }


    /**
     * Xoá một chương trình theo ID.
     */
    public void delete(Long id) {
        donationProgramRepository.deleteById(id);
    }

    /**
     * Tìm kiếm chương trình có ngày trong khoảng chỉ định.
     */
    public List<DonationProgramResponse> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate == null) {
            endDate = startDate;
        }
        List<DonationProgram> programs = donationProgramRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate);
        return programs.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Chuyển từ entity DonationProgram sang DTO để hiển thị.
     */
    private DonationProgramResponse mapToResponseDTO(DonationProgram program) {
        DonationProgramResponse dto = new DonationProgramResponse();
        dto.setId(program.getId());
        dto.setProName(program.getProName());
        dto.setStartDate(program.getStartDate());
        dto.setEndDate(program.getEndDate());
        dto.setDateCreated(program.getDateCreated());
        dto.setStatus(program.getStatus());

        if (program.getAddress() != null) {
            dto.setAddressId(program.getAddress().getId());
        }

        dto.setDescription(program.getDescription());
        dto.setImageUrl(program.getImageUrl());
        dto.setContact(program.getContact());

        // ✅ Gán danh sách typeBloods
        dto.setTypeBloods(program.getTypeBloods());

        if (program.getCity() != null) {
            dto.setCityId(program.getCity().getId());
        }

        if (program.getAdmin() != null) {
            dto.setAdminId(program.getAdmin().getId());
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
