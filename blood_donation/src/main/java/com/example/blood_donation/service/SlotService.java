package com.example.blood_donation.service;

import com.example.blood_donation.dto.RegisterSlotDTO;
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
import java.util.Collections;
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

    public User registerSlot(RegisterSlotDTO dto) {
        User user = userRepository.findById(dto.getUserID())
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
        List<Slot> slots = new ArrayList<>();

        Slot morning = new Slot();
        morning.setStart(LocalTime.of(7, 0));
        morning.setEnd(LocalTime.of(11, 0));
        morning.setLabel("07:00 - 11:00");
        slots.add(morning);

        Slot afternoon = new Slot();
        afternoon.setStart(LocalTime.of(13, 0));
        afternoon.setEnd(LocalTime.of(17, 0));
        afternoon.setLabel("13:00 - 17:00");
        slots.add(afternoon);

        slotRepository.saveAll(slots);
    }

    public Slot createSlot(Slot slot) {
        return slotRepository.save(slot);
    }

}


