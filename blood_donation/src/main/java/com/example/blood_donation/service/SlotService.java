package com.example.blood_donation.service;

import com.example.blood_donation.dto.AppointmentDTO;
import com.example.blood_donation.entity.Slot;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.entity.Appointment;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.AuthenticationRepository;
import com.example.blood_donation.repositoty.SlotRepository;
import com.example.blood_donation.repositoty.UserRepository;
import com.example.blood_donation.repositoty.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AuthenticationRepository authenticationRepository;

    public List<Slot> getSlot() {
        return slotRepository.findAll();
    }

    public User registerSlot(AppointmentDTO dto) {
        User user = userRepository.findByPhone(dto.getPhone())
                .orElseThrow(() -> new BadRequestException("User not found"));

        // ✅ Kiểm tra nếu đã có appointment
        if (user.getAppointment() != null) {
            throw new BadRequestException("You have already registered a donation appointment.");
        }

        // Tìm hoặc tạo appointment theo ngày + slot
        Slot slot = slotRepository.findById(dto.getSlotID())
                .orElseThrow(() -> new BadRequestException("Slot not found"));

        Appointment appointment = appointmentRepository.findByDateAndSlot(dto.getDate(), slot)
                .orElseGet(() -> {
                    Appointment newAppointment = new Appointment();
                    newAppointment.setDate(dto.getDate());
                    newAppointment.setSlot(slot);
                    return appointmentRepository.save(newAppointment);
                });

        // Gán appointment cho user
        user.setAppointment(appointment);
        return userRepository.save(user);
    }




    public void generateSlot() {
//          generate tu dong slot tu 7h sang toi 17h chieu
        LocalTime start = LocalTime.of(7, 0);
        LocalTime end = LocalTime.of(17, 0);
        List<Slot> slots = new ArrayList<>();

        while (start.isBefore(end)) {
            Slot slot = new Slot();
            slot.setStart(start);
            slot.setLabel(start.toString());
            slot.setEnd(start.plusHours(30));

            slots.add(slot);
            start = start.plusMinutes(30);
        }
        slotRepository.saveAll(slots);
    }

    public Slot createSlot(Slot slot) {
        return slotRepository.save(slot);
    }

}


