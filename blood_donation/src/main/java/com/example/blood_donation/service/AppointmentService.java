package com.example.blood_donation.service;

import com.example.blood_donation.dto.AnswerRequest;
import com.example.blood_donation.dto.AppointmentDTO;
import com.example.blood_donation.dto.AppointmentRequest;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ModelMapper modelMapper;

    @Autowired
    private DonationProgramRepository donationProgramRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    /**
     * Tạo appointment mới cho user (MEMBER), chỉ khi không có appointment chưa hoàn thành.
     */
    public AppointmentDTO createAppointment(Long userId, AppointmentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Kiểm tra user đã có appointment active chưa
        boolean hasActiveAppointment = user.getAppointments().stream()
                .anyMatch(a -> a.getStatus() != Status.FULFILLED
                        && a.getStatus() != Status.CANCELLED
                        && a.getStatus() != Status.REJECTED);

        if (hasActiveAppointment) {
            throw new BadRequestException("You already have an active appointment. Complete or cancel it before booking a new one.");
        }

        // Tạo Appointment
        Appointment appointment = buildAppointment(request, user, Status.PENDING);

        // Lưu DB
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Trả về DTO
        AppointmentDTO dto = modelMapper.map(savedAppointment, AppointmentDTO.class);
        dto.setPhone(user.getPhone());
        dto.setAddress(savedAppointment.getProgram().getAddress());
        dto.setTimeRange(savedAppointment.getSlot().getStart() + " - " + savedAppointment.getSlot().getEnd());
        return dto;
    }

    /**
     * Tạo appointment cho staff tạo giùm user qua số điện thoại.
     */
    public AppointmentDTO createAppointmentByPhoneAndProgram(String phone, AppointmentRequest request) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BadRequestException("User not found with phone: " + phone));

        boolean hasActiveAppointment = user.getAppointments().stream()
                .anyMatch(a -> a.getStatus() != Status.FULFILLED
                        && a.getStatus() != Status.CANCELLED
                        && a.getStatus() != Status.REJECTED);

        if (hasActiveAppointment) {
            throw new BadRequestException("User already has an active appointment.");
        }

        Appointment appointment = buildAppointment(request, user, Status.APPROVED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        AppointmentDTO dto = modelMapper.map(savedAppointment, AppointmentDTO.class);
        dto.setPhone(user.getPhone());
        dto.setAddress(savedAppointment.getProgram().getAddress());
        dto.setTimeRange(savedAppointment.getSlot().getStart() + " - " + savedAppointment.getSlot().getEnd());
        return dto;
    }

    /**
     * Cập nhật trạng thái appointment.
     */
    public AppointmentDTO updateStatus(Long appointmentId, Status newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);

        AppointmentDTO dto = modelMapper.map(appointment, AppointmentDTO.class);
        dto.setPhone(appointment.getUser().getPhone());
        dto.setAddress(appointment.getProgram().getAddress());
        dto.setTimeRange(appointment.getSlot().getStart() + " - " + appointment.getSlot().getEnd());
        return dto;
    }

    /**
     * Lấy appointment theo ID.
     */
    public Optional<AppointmentDTO> getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(app -> {
                    AppointmentDTO dto = modelMapper.map(app, AppointmentDTO.class);
                    dto.setPhone(app.getUser().getPhone());
                    dto.setAddress(app.getProgram().getAddress());
                    dto.setTimeRange(app.getSlot().getStart() + " - " + app.getSlot().getEnd());
                    return dto;
                });
    }

    /**
     * Lấy tất cả appointments.
     */
    public List<AppointmentDTO> getAll() {
        return appointmentRepository.findAll().stream()
                .map(app -> {
                    AppointmentDTO dto = modelMapper.map(app, AppointmentDTO.class);
                    dto.setPhone(app.getUser().getPhone());
                    dto.setAddress(app.getProgram().getAddress());
                    dto.setTimeRange(app.getSlot().getStart() + " - " + app.getSlot().getEnd());
                    return dto;
                })
                .toList();
    }

    /**
     * Xóa appointment nếu chưa hoàn thành, có quyền.
     */
    public void deleteAppointmentWithPermission(Long appointmentId, String requesterUsername) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        if (appointment.getStatus() == Status.FULFILLED) {
            throw new BadRequestException("Cannot delete a fulfilled appointment.");
        }

        User owner = appointment.getUser();
        boolean isOwner = owner.getUsername().equals(requesterUsername);
        boolean isAdmin = userRepository.findByUsername(requesterUsername)
                .map(u -> u.getRole().name().equals("ADMIN"))
                .orElse(false);

        if (!isOwner && !isAdmin) {
            throw new BadRequestException("You don't have permission to delete this appointment.");
        }

        owner.getAppointments().remove(appointment);
        appointmentRepository.delete(appointment);
    }

    /**
     * ADMIN xóa appointment bất kỳ.
     */
    public void deleteAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        if (appointment.getStatus() == Status.FULFILLED) {
            throw new BadRequestException("Cannot delete a fulfilled appointment.");
        }

        User user = appointment.getUser();
        if (user != null) {
            user.getAppointments().remove(appointment);
        }

        appointmentRepository.delete(appointment);
    }

    /**
     * Dùng chung logic khởi tạo Appointment.
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

        // Xử lý answers
        if (request.getAnswers() != null) {
            for (AnswerRequest ar : request.getAnswers()) {
                Question q = questionRepository.findById(ar.getQuestionId())
                        .orElseThrow(() -> new BadRequestException("Question not found: " + ar.getQuestionId()));

                Answer answer = new Answer();
                answer.setQuestion(q);

                if (ar.getSelectedOptionIds() != null) {
                    for (Long optionId : ar.getSelectedOptionIds()) {
                        Option opt = optionRepository.findById(optionId)
                                .orElseThrow(() -> new BadRequestException("Option not found: " + optionId));

                        if (Boolean.TRUE.equals(opt.getRequiresText())
                                && (ar.getAdditionalText() == null || ar.getAdditionalText().isBlank())) {
                            throw new BadRequestException("Option '" + opt.getLabel() + "' requires additional text.");
                        }

                        AnswerOption ao = new AnswerOption();
                        ao.setOption(opt);
                        ao.setAdditionalText(ar.getAdditionalText());
                        answer.addAnswerOption(ao);
                    }
                }

                appointment.addAnswer(answer);
            }
        }

        return appointment;
    }

    public List<AppointmentDTO> getByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return user.getAppointments().stream()
                .filter(app -> app.getStatus() != Status.CANCELLED && app.getStatus() != Status.REJECTED)
                .map(app -> {
                    AppointmentDTO dto = modelMapper.map(app, AppointmentDTO.class);
                    dto.setPhone(app.getUser().getPhone());
                    dto.setAddress(app.getProgram().getAddress());
                    dto.setTimeRange(app.getSlot().getStart() + " - " + app.getSlot().getEnd());
                    return dto;
                }).toList();
    }

    /**
     * Trả về toàn bộ lịch sử appointments của user (không lọc trạng thái).
     */
    public List<AppointmentDTO> getFullHistoryByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return user.getAppointments().stream()
                .sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate())) // Sắp xếp mới nhất lên đầu
                .map(app -> {
                    AppointmentDTO dto = modelMapper.map(app, AppointmentDTO.class);
                    dto.setPhone(app.getUser().getPhone());
                    dto.setAddress(app.getProgram().getAddress());
                    dto.setTimeRange(app.getSlot().getStart() + " - " + app.getSlot().getEnd());
                    return dto;
                })
                .toList();
    }


}
