package com.example.blood_donation.service;

import com.example.blood_donation.dto.AppointmentDTO;
import com.example.blood_donation.dto.AppointmentRequest;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private DonationProgramRepository donationProgramRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    /**
     * Tạo appointment mới cho user (MEMBER), chỉ khi không có appointment chưa hoàn thành,
     * và phải cách ít nhất 10 ngày kể từ lần hiến máu trước (FULFILLED).
     */
    public AppointmentDTO createAppointment(Long userId, AppointmentRequest request) {
        System.out.println("=== [DEBUG] Bắt đầu tạo appointment ===");
        System.out.println("UserId: " + userId);
        System.out.println("ProgramId: " + request.getProgramId());
        System.out.println("SlotId: " + request.getSlotId());
        System.out.println("Date: " + request.getDate());
        System.out.println("Answer1: " + request.getAnswer1());
        System.out.println("Answer9: " + request.getAnswer9());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Kiểm tra thông tin cá nhân
        validateUserProfile(user);

        // Kiểm tra tuổi User
        validateUserAge(user);

        // Kiểm tra user đã có appointment đang hoạt động chưa (PENDING hoặc APPROVED)
        boolean hasActiveAppointment = user.getAppointments().stream()
                .anyMatch(a -> a.getStatus() == Status.PENDING || a.getStatus() == Status.APPROVED);

        if (hasActiveAppointment) {
            throw new BadRequestException("You already have an active appointment. Complete or cancel it before booking a new one.");
        }

        // Kiểm tra số ngày từ lần hiến gần nhất
        Optional<Appointment> lastFulfilled = user.getAppointments().stream()
                .filter(a -> a.getStatus() == Status.FULFILLED)
                .max((a1, a2) -> a1.getDate().compareTo(a2.getDate()));

        if (lastFulfilled.isPresent()) {
            LocalDate desiredDate = request.getDate();
            LocalDate lastDonationDate = lastFulfilled.get().getDate();

            long daysBetween = ChronoUnit.DAYS.between(lastDonationDate, desiredDate);

            if (daysBetween < 84) {
                throw new BadRequestException("Bạn chỉ được đặt lịch sau ít nhất 84 ngày kể từ lần hiến máu gần nhất.");
            }
        }

        Appointment appointment = buildAppointment(request, user, Status.PENDING);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        String title = "Tạo lịch hẹn hiến máu thành công";
        String message = "Bạn đã đặt lịch hiến máu vào ngày " + appointment.getDate()
                + " lúc " + appointment.getSlot().getStart() + " - " + appointment.getSlot().getEnd()
                + " tại chương trình \"" + appointment.getProgram().getProName() + "\".";

        notificationService.createNotificationForUser(user, title, message);
        emailService.sendSimpleEmail(user.getEmail(), title, message);


        return mapToDTO(savedAppointment);
    }



    /**
     * Tạo appointment cho staff tạo giùm user qua số điện thoại,
     * chỉ khi user không có appointment đang hoạt động,
     * và cách lần hiến máu gần nhất ít nhất 84 ngày.
     */
    public AppointmentDTO createAppointmentByPhoneAndProgram(String phone, AppointmentRequest request) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BadRequestException("User not found with phone: " + phone));

        // Kiểm tra thông tin cá nhân
        validateUserProfile(user);
        // Kiểm tra tuổi User
        validateUserAge(user);
        // Kiểm tra appointment đang hoạt động
        boolean hasActiveAppointment = user.getAppointments().stream()
                .anyMatch(a -> a.getStatus() == Status.PENDING || a.getStatus() == Status.APPROVED);

        if (hasActiveAppointment) {
            throw new BadRequestException("User already has an active appointment.");
        }

        // Kiểm tra số ngày từ lần hiến máu gần nhất
        Optional<Appointment> lastFulfilled = user.getAppointments().stream()
                .filter(a -> a.getStatus() == Status.FULFILLED)
                .max((a1, a2) -> a1.getDate().compareTo(a2.getDate()));

        if (lastFulfilled.isPresent()) {
            LocalDate desiredDate = request.getDate();
            LocalDate lastDonationDate = lastFulfilled.get().getDate();

            long daysBetween = ChronoUnit.DAYS.between(lastDonationDate, desiredDate);

            if (daysBetween < 84) {
                throw new BadRequestException("Người dùng chỉ được đặt lịch sau ít nhất 84 ngày kể từ lần hiến máu gần nhất.");
            }
        }

        Appointment appointment = buildAppointment(request, user, Status.APPROVED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        String title = "Tạo lịch hẹn hiến máu thành công";
        String message = "Bạn đã đặt lịch hiến máu vào ngày " + appointment.getDate()
                + " lúc " + appointment.getSlot().getStart() + " - " + appointment.getSlot().getEnd()
                + " tại chương trình \"" + appointment.getProgram().getProName() + "\".";

        notificationService.createNotificationForUser(user, title, message);
        emailService.sendSimpleEmail(user.getEmail(), title, message);

        return mapToDTO(savedAppointment);
    }



    /**
     * Kiểm tra thông tin cá nhân bắt buộc của user
     */
    private void validateUserProfile(User user) {
        if (user.getCccd() == null || user.getCccd().isBlank()
                || user.getBirthdate() == null
                || user.getGender() == null
                || user.getTypeBlood() == null
                || user.getAddress() == null || user.getAddress().getName() == null || user.getAddress().getName().isBlank()
                || user.getPhone() == null || user.getPhone().isBlank()) {
            throw new BadRequestException("Vui lòng cập nhật thông tin cá nhân đầy đủ trước khi đặt lịch.");
        }
    }

    /**
     * Cập nhật trạng thái appointment.
     */
    public AppointmentDTO updateStatus(Long appointmentId, Status newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        appointment.setStatus(newStatus);
        // Gửi thông báo nếu trạng thái mới là FULFILLED
        appointmentRepository.save(appointment);
        if (newStatus == Status.FULFILLED) {
            User user = appointment.getUser();
            String title = "Cảm ơn bạn đã hiến máu";
            String message = "Cảm ơn bạn đã hoàn thành lịch hiến máu ngày " + appointment.getDate()
                    + ". Sự đóng góp của bạn rất quý giá với cộng đồng.";

            notificationService.createNotificationForUser(user, title, message);
            emailService.sendSimpleEmail(user.getEmail(), title, message);
        }
        // ✅ Gửi thông báo nếu trạng thái mới là APPROVED hoặc REJECTED
        if (newStatus == Status.APPROVED || newStatus == Status.REJECTED) {
            User user = appointment.getUser();
            String statusStr = (newStatus == Status.APPROVED) ? "được CHẤP THUẬN" : "bị TỪ CHỐI";
            String title = "Lịch hẹn hiến máu của bạn đã " + statusStr;
            String message = "Lịch hẹn hiến máu ngày " + appointment.getDate() + " đã " + statusStr.toLowerCase() + " bởi nhân viên.";

            notificationService.createNotificationForUser(user, title, message);
            emailService.sendSimpleEmail(user.getEmail(), title, message);
        }

        return mapToDTO(appointment);
    }

    /**
     * Lấy appointment theo ID.
     */
    public Optional<AppointmentDTO> getAppointmentById(Long id) {
        return appointmentRepository.findById(id).map(this::mapToDTO);
    }

    /**
     * Lấy tất cả appointments.
     */
    public List<AppointmentDTO> getAll() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Xóa appointment nếu chưa hoàn thành, có quyền.
     */
    public void deleteAppointmentWithPermission(Long appointmentId, Long requesterUserID) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        if (appointment.getStatus() == Status.FULFILLED) {
            throw new BadRequestException("Cannot delete a fulfilled appointment.");
        }

        User requester = userRepository.findById(requesterUserID)
                .orElseThrow(() -> new BadRequestException("Requester not found"));

        User owner = appointment.getUser();
        boolean isOwner = owner.getUsername().equals(requesterUserID);

        String role = requester.getRole().name();
        boolean hasPermission = isOwner || role.equals("MEMBER") || role.equals("STAFF") || role.equals("HOSPITAL_STAFF");

        if (!hasPermission) {
            throw new BadRequestException("You don't have permission to delete this appointment.");
        }

        appointmentRepository.delete(appointment);
    }


    /**
     * HOSPITAL_STAFF hoặc STAFF xóa appointment bất kỳ.
     */
    public void deleteAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        if (appointment.getStatus() == Status.FULFILLED) {
            throw new BadRequestException("Cannot delete a fulfilled appointment.");
        }

        appointmentRepository.delete(appointment);
    }

    /**
     *  Tạo Appointment.
     */
    private Appointment buildAppointment(AppointmentRequest request, User user, Status status) {
        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new BadRequestException("Slot not found"));
        // Nếu đặt lịch trong ngày hôm nay thì kiểm tra giờ hiện tại
        if (request.getDate().isEqual(LocalDate.now())) {
            LocalTime now = LocalTime.now();

            if (now.isAfter(slot.getEnd())) {
                throw new BadRequestException("Khung giờ hiến máu đã kết thúc. Vui lòng chọn thời gian khác.");
            }
        }


        DonationProgram program = donationProgramRepository.findById(request.getProgramId())
                .orElseThrow(() -> new BadRequestException("Program not found"));

        Appointment appointment = new Appointment();
        appointment.setDate(request.getDate());
        appointment.setSlot(slot);
        appointment.setProgram(program);
        appointment.setStatus(status);
        appointment.setUser(user);

        // Gán 10 câu trả lời
        appointment.setAnswer1(request.getAnswer1());
        appointment.setAnswer2(request.getAnswer2());
        appointment.setAnswer3(request.getAnswer3());
        appointment.setAnswer4(request.getAnswer4());
        appointment.setAnswer5(request.getAnswer5());
        appointment.setAnswer6(request.getAnswer6());
        appointment.setAnswer7(request.getAnswer7());
        appointment.setAnswer8(request.getAnswer8());
        appointment.setAnswer9(request.getAnswer9());
        appointment.setAnswer10(request.getAnswer10());

        return appointment;
    }

    public List<AppointmentDTO> getByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return user.getAppointments().stream()
                .filter(app -> app.getStatus() != Status.CANCELLED && app.getStatus() != Status.REJECTED)
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Trả về toàn bộ lịch sử appointments của user (không lọc trạng thái).
     */
    public List<AppointmentDTO> getFullHistoryByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return user.getAppointments().stream()
                .sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate())) // Sắp xếp mới nhất lên đầu
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Chuyển đổi sang DTO
     */
    private AppointmentDTO mapToDTO(Appointment app) {
        AppointmentDTO dto = modelMapper.map(app, AppointmentDTO.class);
        dto.setPhone(app.getUser().getPhone());
        if (app.getProgram().getAddress() != null) {
            dto.setAddress(app.getProgram().getAddress().getName());
        }
        dto.setTimeRange(app.getSlot().getStart() + " - " + app.getSlot().getEnd());
        return dto;
    }
    /**
     * Member hủy lịch hẹn
     */
    public AppointmentDTO cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        if (appointment.getUser().getId() != userId) {
            throw new BadRequestException("Bạn không có quyền hủy lịch hẹn này.");
        }

        if (appointment.getStatus() == Status.FULFILLED) {
            throw new BadRequestException("Không thể hủy lịch đã hoàn tất.");
        }

        appointment.setStatus(Status.CANCELLED);
        User user = appointment.getUser();
        String title = "Lịch hẹn hiến máu đã được hủy";
        String message = "Bạn đã hủy lịch hiến máu ngày " + appointment.getDate()
                + ". Nếu bạn có thay đổi kế hoạch, đừng quên đặt lịch mới nhé.";

        notificationService.createNotificationForUser(user, title, message);
        emailService.sendSimpleEmail(user.getEmail(), title, message);

        appointmentRepository.save(appointment);

        return mapToDTO(appointment);
    }


    /**
     * Tính số ngày còn lại trước khi hiến máu
     */
    public int calculateDaysLeft(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        LocalDate today = LocalDate.now();
        LocalDate donationDate = appointment.getDate();

        if (donationDate.isBefore(today)) {
            throw new BadRequestException("Lịch hẹn này đã qua ngày hiến máu.");
        }
        return (int) ChronoUnit.DAYS.between(today, donationDate);
    }

    /**
     * Kiểm tra tuổi user
     */
    private void validateUserAge(User user) {
        if (user.getBirthdate() == null) {
            throw new BadRequestException("Thiếu ngày sinh. Vui lòng cập nhật ngày sinh trước khi đặt lịch.");
        }

        int age = (int) ChronoUnit.YEARS.between(user.getBirthdate(), LocalDate.now());

        if (age < 18 || age >= 60) {
            throw new BadRequestException("Chỉ những người từ 18 đến dưới 60 tuổi mới được phép đăng ký hiến máu.");
        }
    }

    /**
     * Tự động chuyển trạng thái các lịch hẹn quá hạn thành REJECTED (chạy mỗi ngày lúc 1h sáng)
     */
    @Scheduled(cron = "0 0 1 * * *") // Mỗi ngày lúc 01:00 sáng
    @Transactional
    public void autoRejectExpiredAppointments() {
        LocalDate today = LocalDate.now();

        List<Appointment> expiredAppointments = appointmentRepository.findExpiredAppointments(today);

        for (Appointment appointment : expiredAppointments) {
            appointment.setStatus(Status.REJECTED);
        }

        appointmentRepository.saveAll(expiredAppointments);

        System.out.println("Đã cập nhật " + expiredAppointments.size() + " lịch hẹn quá hạn sang REJECTED.");
    }
}
