package com.example.blood_donation.service;

import com.example.blood_donation.dto.DonationProgramDTO;
import com.example.blood_donation.entity.DonationProgram;
import com.example.blood_donation.entity.City;
import com.example.blood_donation.entity.Slot;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.repositoty.DonationProgramRepository;
import com.example.blood_donation.repositoty.CityRepository;
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
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Lấy danh sách tất cả chương trình hiến máu.
     *
     * @return danh sách DTO của các chương trình.
     */
    public List<DonationProgramDTO> getAll() {
        return donationProgramRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Lấy thông tin chương trình theo ID.
     *
     * @param id ID của chương trình.
     * @return DTO tương ứng.
     */
    public DonationProgramDTO getById(Long id) {
        DonationProgram program = donationProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));
        return mapToDTO(program);
    }

    /**
     * Tìm kiếm các chương trình có ngày diễn ra nằm trong khoảng startDate - endDate
     * và thuộc City chỉ định.
     */
    public List<DonationProgramDTO> searchByDateInRangeAndCityID(LocalDate date, Long cityId) {
        List<DonationProgram> programs = donationProgramRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCity_Id(
                        date, date, cityId
                );
        return programs.stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Tạo mới một chương trình hiến máu và gán người tạo là Admin hiện tại.
     */
    @Transactional
    public DonationProgramDTO create(DonationProgramDTO dto, String adminUsername) {
        DonationProgram program = new DonationProgram();
        program.setProName(dto.getProName());
        program.setStartDate(dto.getStartDate());
        program.setEndDate(dto.getEndDate());
        program.setDateCreated(LocalDate.now());
        program.setAddress(dto.getAddress());
        program.setLatitude(dto.getLatitude());
        program.setLongitude(dto.getLongitude());
        program.setTypeBlood(dto.getTypeBlood());
        program.setDescription(dto.getDescription());
        program.setContact(dto.getContact());
        program.setImageUrl(dto.getImageUrl());

        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("City not found"));
            program.setCity(city);
        }

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));
        program.setAdmin(admin);

        if (dto.getSlotIds() != null && !dto.getSlotIds().isEmpty()) {
            List<Slot> slots = slotRepository.findAllById(dto.getSlotIds());
            program.setSlots(slots); // ✅ không còn setProgram nữa
        }

        DonationProgram saved = donationProgramRepository.save(program);
        return mapToDTO(saved);
    }

    /**
     * Cập nhật thông tin chương trình đã có.
     */
    public DonationProgramDTO update(Long id, DonationProgramDTO dto) {
        DonationProgram existing = donationProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));

        // Cập nhật thông tin cơ bản
        existing.setProName(dto.getProName());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setAddress(dto.getAddress());
        existing.setLatitude(dto.getLatitude());
        existing.setLongitude(dto.getLongitude());
        existing.setTypeBlood(dto.getTypeBlood());
        existing.setDescription(dto.getDescription());
        existing.setContact(dto.getContact());
        existing.setImageUrl(dto.getImageUrl());

        // Cập nhật địa điểm nếu có
        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("City not found"));
            existing.setCity(city);
        }

        // Cập nhật danh sách Slot nếu có
        if (dto.getSlotIds() != null) {
            List<Slot> updatedSlots = slotRepository.findAllById(dto.getSlotIds());
            existing.setSlots(updatedSlots); // ✅ chỉ gán danh sách, không xử lý orphan
        }

        DonationProgram updated = donationProgramRepository.save(existing);
        return mapToDTO(updated);
    }



    /**
     * Xoá một chương trình theo ID.
     */
    public void delete(Long id) {
        donationProgramRepository.deleteById(id);
    }

    /**
     * Tìm kiếm chương trình có ngày trong khoảng chỉ định
     */
    public List<DonationProgramDTO> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate == null) {
            endDate = startDate;
        }
        List<DonationProgram> programs = donationProgramRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate);
        return programs.stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Chuyển từ entity DonationProgram sang DTO.
     */
    /**
     * Chuyển từ entity DonationProgram sang DTO.
     */
    private DonationProgramDTO mapToDTO(DonationProgram program) {
        DonationProgramDTO dto = new DonationProgramDTO();
        dto.setId(program.getId());
        dto.setProName(program.getProName());
        dto.setStartDate(program.getStartDate());
        dto.setEndDate(program.getEndDate());
        dto.setDateCreated(program.getDateCreated());
        dto.setAddress(program.getAddress());
        dto.setLatitude(program.getLatitude());
        dto.setLongitude(program.getLongitude());

        // ✅ Thêm mới
        dto.setDescription(program.getDescription());
        dto.setImageUrl(program.getImageUrl());
        dto.setContact(program.getContact());
        dto.setTypeBlood(program.getTypeBlood());

        if (program.getCity() != null) {
            dto.setCityId(program.getCity().getId());
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
