package com.example.blood_donation.service;

import com.example.blood_donation.dto.AnswerRequest;
import com.example.blood_donation.dto.AppointmentDTO;
import com.example.blood_donation.dto.AppointmentRequest;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.enums.QuestionType;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    DonationProgramRepository donationProgramRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    /**
     * Tạo một appointment mới cho User nếu chưa có active appointment.
     */
    public AppointmentDTO createAppointment(Long userId, AppointmentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getAppointment() != null && user.getAppointment().getStatus() != Status.FULFILLED) {
            throw new BadRequestException("You already have an active appointment");
        }

        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new BadRequestException("Slot not found"));

        DonationProgram program = donationProgramRepository.findById(request.getProgramId())
                .orElseThrow(() -> new BadRequestException("Program not found"));

        Appointment appointment = new Appointment();
        appointment.setDate(request.getDate());
        appointment.setSlot(slot);
        appointment.setProgram(program);
        appointment.setStatus(Status.PENDING);
        appointment.setUser(user);

        // Lưu câu trả lời
        if (request.getAnswers() != null) {
            for (AnswerRequest ar : request.getAnswers()) {
                Question question = questionRepository.findById(ar.getQuestionId())
                        .orElseThrow(() -> new BadRequestException("Question not found: " + ar.getQuestionId()));

                Answer answer = new Answer();
                answer.setQuestion(question);
                appointment.addAnswer(answer);

                if (ar.getSelectedOptionIds() != null) {
                    for (Long optionId : ar.getSelectedOptionIds()) {
                        Option opt = optionRepository.findById(optionId)
                                .orElseThrow(() -> new BadRequestException("Option not found: " + optionId));

                        // Validate nếu option yêu cầu text nhưng không nhập
                        if (Boolean.TRUE.equals(opt.getRequiresText()) && (ar.getAdditionalText() == null || ar.getAdditionalText().isBlank())) {
                            throw new BadRequestException("Option '" + opt.getLabel() + "' requires additional text");
                        }

                        AnswerOption ao = new AnswerOption();
                        ao.setOption(opt);
                        ao.setAdditionalText(ar.getAdditionalText());
                        answer.addAnswerOption(ao);
                    }
                }
            }
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        user.setAppointment(savedAppointment);
        userRepository.save(user);

        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(savedAppointment.getId());
        dto.setDate(savedAppointment.getDate());
        dto.setStatus(savedAppointment.getStatus());
        dto.setPhone(user.getPhone());
        return dto;
    }


    /**
     * Tạo một appointment mới cho User bằng role Hospital-staff nếu chưa có active appointment.
     */
    public AppointmentDTO createAppointmentByPhoneAndProgram(String phone, AppointmentRequest request) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BadRequestException("User not found with phone: " + phone));

        if (user.getAppointment() != null && user.getAppointment().getStatus() != Status.FULFILLED) {
            throw new BadRequestException("User already has an active appointment");
        }

        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new BadRequestException("Slot not found"));

        DonationProgram program = donationProgramRepository.findById(request.getProgramId())
                .orElseThrow(() -> new BadRequestException("Program not found"));

        Appointment appointment = new Appointment();
        appointment.setDate(request.getDate());
        appointment.setSlot(slot);
        appointment.setProgram(program);
        appointment.setStatus(Status.APPROVED);
        appointment.setUser(user);

        // Lưu câu trả lời
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

                        // Validate requiresText
                        if (Boolean.TRUE.equals(opt.getRequiresText()) &&
                                (ar.getAdditionalText() == null || ar.getAdditionalText().isBlank())) {
                            throw new BadRequestException("Option '" + opt.getLabel() + "' requires additional text");
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

        user.setAppointment(appointment);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        AppointmentDTO dto = modelMapper.map(savedAppointment, AppointmentDTO.class);
        dto.setPhone(user.getPhone());
        return dto;
    }


    /**
     * Cập nhật trạng thái của một appointment.
     */
    public AppointmentDTO updateStatus(Long appointmentId, Status newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
        return modelMapper.map(appointment, AppointmentDTO.class);
    }

    /**
     * Lấy appointment theo ID.
     */
    public Optional<AppointmentDTO> getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(app -> modelMapper.map(app, AppointmentDTO.class));
    }

    /**
     * Lấy danh sách tất cả các appointment.
     */
    public List<AppointmentDTO> getAll() {
        return appointmentRepository.findAll()
                .stream()
                .map(app -> modelMapper.map(app, AppointmentDTO.class))
                .toList();
    }

    /**
     * Xóa appointment bởi ADMIN hoặc chính user tạo nó, nếu chưa hoàn thành.
     */
    public void deleteAppointmentWithPermission(Long appointmentId, String requesterUsername) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        User owner = appointment.getUser();

        boolean isOwner = owner.getUsername().equals(requesterUsername);
        boolean isAdmin = userRepository.findByUsername(requesterUsername)
                .map(u -> u.getRole().name().equals("ADMIN"))
                .orElse(false);

        if (!isOwner && !isAdmin) {
            throw new BadRequestException("You don't have permission to delete this appointment.");
        }

        if (appointment.getStatus() == Status.FULFILLED) {
            throw new BadRequestException("Cannot delete a fulfilled appointment.");
        }

        // Unlink user <-> appointment
        if (owner != null) {
            owner.setAppointment(null);
            userRepository.save(owner);
        }

        appointmentRepository.delete(appointment);
    }

    /**
     * ADMIN có thể xóa trực tiếp bất kỳ appointment nào (không gọi từ controller).
     */
    public void deleteAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Appointment not found"));

        if (appointment.getStatus() == Status.FULFILLED) {
            throw new BadRequestException("Cannot delete a fulfilled appointment.");
        }

        User user = appointment.getUser();
        if (user != null) {
            user.setAppointment(null);
            userRepository.save(user);
        }

        appointmentRepository.delete(appointment);
    }
}
