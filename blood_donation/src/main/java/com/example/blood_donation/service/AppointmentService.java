package com.example.blood_donation.service;

import com.example.blood_donation.dto.AppointmentDTO;
import com.example.blood_donation.dto.AppointmentRequest;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        return mapToDTO(savedAppointment);
    }



    /**
     * Tạo appointment cho staff tạo giùm user qua số điện thoại,
     * chỉ khi user không có appointment đang hoạt động,
     * và cách lần hiến máu gần nhất ít nhất 10 ngày.
     */
    public AppointmentDTO createAppointmentByPhoneAndProgram(String phone, AppointmentRequest request) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BadRequestException("User not found with phone: " + phone));

        // Kiểm tra thông tin cá nhân
        validateUserProfile(user);

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
        appointmentRepository.save(appointment);

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

        DonationProgram program = donationProgramRepository.findById(request.getProgramId())
                .orElseThrow(() -> new BadRequestException("Program not found"));

        Appointment appointment = new Appointment();
        appointment.setDate(request.getDate());
        appointment.setSlot(slot);
        appointment.setProgram(program);
        appointment.setStatus(status);
        appointment.setUser(user);

        // Gán 9 câu trả lời
        appointment.setAnswer1(request.getAnswer1());
        appointment.setAnswer2(request.getAnswer2());
        appointment.setAnswer3(request.getAnswer3());
        appointment.setAnswer4(request.getAnswer4());
        appointment.setAnswer5(request.getAnswer5());
        appointment.setAnswer6(request.getAnswer6());
        appointment.setAnswer7(request.getAnswer7());
        appointment.setAnswer8(request.getAnswer8());
        appointment.setAnswer9(request.getAnswer9());

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
        appointmentRepository.save(appointment);

        return mapToDTO(appointment);
    }

}
