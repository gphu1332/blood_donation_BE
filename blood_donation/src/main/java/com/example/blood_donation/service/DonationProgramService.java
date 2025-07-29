package com.example.blood_donation.service;

import com.example.blood_donation.dto.DonationProgramDTO;
import com.example.blood_donation.dto.DonationProgramResponse;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonationProgramService {
    @Autowired
    private AppointmentRepository appointmentRepository;

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
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    // Lấy danh sách tất cả chương trình hiến máu.
    public List<DonationProgramResponse> getAll() {
        return donationProgramRepository.findAll().stream()
                .filter(program -> !program.isDeleted())
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Lấy thông tin chương trình theo ID.
    public DonationProgramResponse getById(Long id) {
        DonationProgram program = donationProgramRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));
        return mapToResponseDTO(program);
    }

    // Tìm kiếm chương trình theo ngày và cityId.
    public List<DonationProgramResponse> searchByDateInRangeAndCityID(LocalDate date, Long cityId) {
        City city = cityRepository.findById(cityId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("City not found or has been deleted"));

        return donationProgramRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCity_Id(date, date, city.getId())
                .stream()
                .filter(p -> !p.isDeleted())
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Tạo chương trình mới
    @Transactional
    public DonationProgramResponse create(DonationProgramDTO dto, String adminUsername) {
        if (dto.getAddressId() != null) {
            List<DonationProgram> conflicts = donationProgramRepository
                    .findConflictingPrograms(dto.getAddressId(), dto.getStartDate(), dto.getEndDate())
                    .stream()
                    .filter(p -> !p.isDeleted())
                    .toList();

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
        program.setDeleted(false);

        // Liên kết City
        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .filter(c -> !c.isDeleted())
                    .orElseThrow(() -> new EntityNotFoundException("City not found or has been deleted"));
            program.setCity(city);
        }

        // Liên kết Adress
        if (dto.getAddressId() != null) {
            Adress address = adressRepository.findById(dto.getAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Address not found"));
            program.setAddress(address);
        }

        // Liên kết Admin
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));
        program.setAdmin(admin);

        // Liên kết các Slot
        if (dto.getSlotIds() != null && !dto.getSlotIds().isEmpty()) {
            List<Slot> slots = slotRepository.findAllById(dto.getSlotIds());
            program.setSlots(slots);
        }

        DonationProgram saved = donationProgramRepository.save(program);
        return mapToResponseDTO(saved);
    }

    // Cập nhật chương trình
    @Transactional
    public DonationProgramResponse update(Long id, DonationProgramDTO dto) {
        DonationProgram existing = donationProgramRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));

        // Kiểm tra trùng lịch
        if (dto.getAddressId() != null) {
            List<DonationProgram> conflicts = donationProgramRepository
                    .findConflictingProgramsExcludingSelf(dto.getAddressId(), dto.getStartDate(), dto.getEndDate(), id)
                    .stream()
                    .filter(p -> !p.isDeleted())
                    .toList();

            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Đã có chương trình tại địa chỉ này trong khoảng thời gian đã chọn.");
            }
        }

        // So sánh thông tin quan trọng
        boolean isImportantChanged =
                !existing.getProName().equals(dto.getProName()) ||
                        !existing.getStartDate().equals(dto.getStartDate()) ||
                        !existing.getEndDate().equals(dto.getEndDate()) ||
                        !existing.getDescription().equals(dto.getDescription()) ||
                        (existing.getAddress() != null && !existing.getAddress().getId().equals(dto.getAddressId())) ||
                        (dto.getSlotIds() != null && !existing.getSlots().stream().map(Slot::getSlotID).toList().equals(dto.getSlotIds()));

        // Cập nhật các trường
        existing.setProName(dto.getProName());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setTypeBloods(dto.getTypeBloods());
        existing.setDescription(dto.getDescription());
        existing.setContact(dto.getContact());
        existing.setImageUrl(dto.getImageUrl());

        // Liên kết City
        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("City not found"));
            existing.setCity(city);
        }

        // Liên kết Address
        if (dto.getAddressId() != null) {
            Adress address = adressRepository.findById(dto.getAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Address not found"));
            existing.setAddress(address);
        }

        // Cập nhật Slot
        if (dto.getSlotIds() != null) {
            List<Slot> updatedSlots = slotRepository.findAllById(dto.getSlotIds());
            existing.setSlots(updatedSlots);
        }

        // Lưu chương trình đã cập nhật
        DonationProgram updated = donationProgramRepository.save(existing);

        // Gửi mail + Notification nếu có thay đổi quan trọng
        if (isImportantChanged) {
            List<Appointment> appointments = appointmentRepository.findByProgram_Id(id).stream()
                    .filter(a ->
                            a.getStatus() == Status.PENDING ||
                            a.getStatus() == Status.APPROVED)
                    .toList();

            for (Appointment appointment : appointments) {
                User user = appointment.getUser();

                String location = updated.getAddress() != null
                        ? updated.getAddress().getName()
                        : "Không xác định";

                emailService.sendProgramUpdateEmail(
                        user.getEmail(),
                        user.getFullName(),
                        updated.getProName(),
                        updated.getStartDate(),
                        updated.getEndDate(),
                        location,
                        "Thông tin chương trình bạn đã đăng ký đã có thay đổi quan trọng. Vui lòng kiểm tra lại."
                );

                notificationService.createNotificationForUser(
                        user,
                        "Chương trình đã được cập nhật",
                        "Chương trình " + updated.getProName() + " mà bạn đã đăng ký đã có thay đổi. Vui lòng kiểm tra lại thông tin chi tiết của chương trình."
                );
            }
        }


        return mapToResponseDTO(updated);
    }

    // Xoá mềm chương trình
    public void delete(Long id) {
        DonationProgram program = donationProgramRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Donation program not found"));

        program.setDeleted(true);
        donationProgramRepository.save(program);

        // Gửi email + Notification thông báo hủy
        List<Appointment> appointments = appointmentRepository.findByProgram_Id(id).stream()
                .filter(a -> a.getStatus() == Status.APPROVED)
                .toList();

        for (Appointment appointment : appointments) {
            User user = appointment.getUser();

            String location = program.getAddress() != null
                    ? program.getAddress().getName()
                    : "Không xác định";

            emailService.sendProgramDeletedEmail(
                    user.getEmail(),
                    user.getFullName(),
                    program.getProName(),
                    program.getStartDate(),
                    program.getEndDate(),
                    location
            );

            notificationService.createNotificationForUser(
                    user,
                    "Chương trình đã bị hủy",
                    "Chúng tôi rất tiếc phải thông báo rằng chương trình " + program.getProName() + " tại " + location + " đã bị hủy."
            );
        }

    }

    // Tìm kiếm theo ngày
    public List<DonationProgramResponse> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate == null) {
            endDate = startDate;
        }

        return donationProgramRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate)
                .stream()
                .filter(p -> !p.isDeleted())
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Ánh xạ sang Response DTO
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
        dto.setTypeBloods(program.getTypeBloods());

        if (program.getCity() != null && !program.getCity().isDeleted()) {
            dto.setCityId(program.getCity().getId());
        } else {
            dto.setCityId(null);
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
