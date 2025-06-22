package com.example.blood_donation.service;

import com.example.blood_donation.dto.AppointmentDTO;
import com.example.blood_donation.entity.Appointment;
import com.example.blood_donation.entity.Slot;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.AppointmentRepository;
import com.example.blood_donation.repositoty.SlotRepository;
import com.example.blood_donation.repositoty.UserRepository;
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

    /**
     * Tạo một appointment mới cho User nếu chưa có active appointment.
     */
    public AppointmentDTO createAppointment(Long userId, Long slotId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getAppointment() != null && user.getAppointment().getStatus() != Status.FULFILLED) {
            throw new BadRequestException("You already have an active appointment");
        }

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new BadRequestException("Slot not found"));

        Appointment appointment = new Appointment();
        appointment.setDate(date);
        appointment.setSlot(slot);
        appointment.setStatus(Status.PENDING);

        Appointment saved = appointmentRepository.save(appointment);

        user.setAppointment(saved);
        userRepository.save(user);

        return modelMapper.map(saved, AppointmentDTO.class);
    }
    /**
     * Tạo một appointment mới cho User bằng role Admin nếu chưa có active appointment.
     */
    public AppointmentDTO createAppointmentByPhone(String phone, Long slotId, LocalDate date) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BadRequestException("User with phone " + phone + " not found"));

        if (user.getAppointment() != null && user.getAppointment().getStatus() != Status.FULFILLED) {
            throw new BadRequestException("User already has an active appointment");
        }

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new BadRequestException("Slot not found"));

        Appointment appointment = new Appointment();
        appointment.setDate(date);
        appointment.setSlot(slot);
        appointment.setStatus(Status.APPROVED); // Vì là Admin nên được duyệt luôn

        Appointment saved = appointmentRepository.save(appointment);

        user.setAppointment(saved);
        userRepository.save(user);

        return modelMapper.map(saved, AppointmentDTO.class);
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
