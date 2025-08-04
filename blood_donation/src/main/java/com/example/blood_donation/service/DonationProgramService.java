package com.example.blood_donation.service;

import com.example.blood_donation.dto.BloodGroupStats;
import com.example.blood_donation.dto.DonationProgramDTO;
import com.example.blood_donation.dto.DonationProgramResponse;
import com.example.blood_donation.dto.ProgramStatisticsDTO;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.enums.TypeBlood;
import com.example.blood_donation.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DonationProgramService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DonationProgramRepository donationProgramRepository;

    @Autowired
    private DonationDetailRepository donationDetailRepository;

    @Autowired
    private BloodUnitRepository bloodUnitRepository;

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
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương trình hiến máu"));
        return mapToResponseDTO(program);
    }

    // Tìm kiếm chương trình theo ngày và cityId.
    public List<DonationProgramResponse> searchByDateInRangeAndCityID(LocalDate date, Long cityId) {
        City city = cityRepository.findById(cityId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Thành phố không tìm thấy hoặc đã bị xóa"));

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
        program.setMaxParticipant(dto.getMaxParticipant());
        program.setDeleted(false);

        // Liên kết City
        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .filter(c -> !c.isDeleted())
                    .orElseThrow(() -> new EntityNotFoundException("Thành phố không tìm thấy hoặc đã bị xóa"));
            program.setCity(city);
        }

        // Liên kết Adress
        if (dto.getAddressId() != null) {
            Adress address = adressRepository.findById(dto.getAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ"));
            program.setAddress(address);
        }

        // Liên kết Admin
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng quản trị"));
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
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương trình hiến máu"));

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
                !Objects.equals(existing.getProName(), dto.getProName()) ||
                        !Objects.equals(existing.getStartDate(), dto.getStartDate()) ||
                        !Objects.equals(existing.getEndDate(), dto.getEndDate()) ||
                        !Objects.equals(existing.getDescription(), dto.getDescription()) ||
                        (existing.getAddress() != null && !Objects.equals(existing.getAddress().getId(), dto.getAddressId())) ||
                        (dto.getSlotIds() != null && !existing.getSlots().stream().map(Slot::getSlotID).toList().equals(dto.getSlotIds()));

        // Cập nhật các trường
        existing.setProName(dto.getProName());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setTypeBloods(dto.getTypeBloods());
        existing.setDescription(dto.getDescription());
        existing.setContact(dto.getContact());
        existing.setImageUrl(dto.getImageUrl());
        existing.setMaxParticipant(dto.getMaxParticipant());

        // Liên kết City
        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thành phố"));
            existing.setCity(city);
        }

        // Liên kết Address
        if (dto.getAddressId() != null) {
            Adress address = adressRepository.findById(dto.getAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ"));
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
                        "Chương trình mà bạn đăng ký đã được cập nhật",
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
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương trình hiến máu"));

        program.setDeleted(true);
        donationProgramRepository.save(program);

        // Gửi email + Notification thông báo hủy
        List<Appointment> appointments = appointmentRepository.findByProgram_Id(id).stream()
                .filter(a ->
                        a.getStatus() == Status.PENDING ||
                        a.getStatus() == Status.APPROVED)
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
                    "Chương trình mà bạn đăng ký đã bị hủy",
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
        dto.setMaxParticipant(program.getMaxParticipant());

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
        long registeredCount = appointmentRepository.countActiveAppointmentsByProgram(program.getId());
        dto.setRegisteredCount(registeredCount);
        return dto;
    }

    // Thong ke chi so cho chuong trinh
    public ProgramStatisticsDTO getStatisticsByProgramId(Long programId) {
        DonationProgram program = donationProgramRepository.findById(programId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương trình hiến máu"));

        List<Appointment> appointments = appointmentRepository.findByProgram_Id(programId);
        long totalAppointments = appointments.size();

        int cancelledCount = 0;
        int rejectedCount = 0;
        int approvedCount = 0;
        int pendingCount = 0;
        int fulfilledCount = 0;
        double successRate = 0.0;
        double failRate = 0.0;

        for (Appointment appointment : appointments) {
            switch (appointment.getStatus()) {
                case CANCELLED -> cancelledCount++;
                case REJECTED -> rejectedCount++;
                case APPROVED -> approvedCount++;
                case PENDING -> pendingCount++;
                case FULFILLED -> fulfilledCount++;
            }
        }

        List<Long> appointmentIds = appointments.stream().map(Appointment::getId).toList();
        List<DonationDetail> donationDetails = donationDetailRepository.findByAppointment_IdIn(appointmentIds);
        List<Long> donationDetailIds = donationDetails.stream()
                .map(DonationDetail::getDonID)
                .toList();

        List<BloodUnit> bloodUnits = bloodUnitRepository.findByDonationDetail_DonIDIn(donationDetailIds);

        int totalBags = 0;
        int totalVolume = 0;

        // Khởi tạo map thống kê nhóm máu
        Map<TypeBlood, BloodGroupStats> bloodStatsMap = new EnumMap<>(TypeBlood.class);

        for (BloodUnit unit : bloodUnits) {
            TypeBlood bloodType = unit.getTypeBlood();
            int volume = unit.getVolume(); // 200, 350, 500

            if (bloodType == null || volume <= 0) continue;

            bloodStatsMap.putIfAbsent(bloodType, new BloodGroupStats());

            BloodGroupStats stats = bloodStatsMap.get(bloodType);

            // Đếm số lượng theo thể tích
            switch (volume) {
                case 200 -> stats.setQuantity200ml(stats.getQuantity200ml() + 1);
                case 350 -> stats.setQuantity350ml(stats.getQuantity350ml() + 1);
                case 500 -> stats.setQuantity500ml(stats.getQuantity500ml() + 1);
            }

            stats.setTotalBags(stats.getTotalBags() + 1);
            stats.setTotalVolume(stats.getTotalVolume() + volume);

            totalBags++;
            totalVolume += volume;
        }
        //tỉ lệ thành công và thất bại
        successRate = totalAppointments == 0 ? 0.0 : (double) fulfilledCount / totalAppointments;
        failRate = totalAppointments == 0 ? 0.0 : (double) (cancelledCount + rejectedCount) / totalAppointments;

        return new ProgramStatisticsDTO(
                programId,
                totalAppointments,
                cancelledCount,
                rejectedCount,
                approvedCount,
                pendingCount,
                fulfilledCount,
                successRate,
                failRate,
                totalBags,
                totalVolume,
                bloodStatsMap
        );
    }


}
