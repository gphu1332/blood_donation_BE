package com.example.blood_donation.service;

import com.example.blood_donation.dto.CreateDonationDetailDTO;
import com.example.blood_donation.dto.DonationDetailDTO;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repository.AppointmentRepository;
import com.example.blood_donation.repository.DonationDetailRepository;
import com.example.blood_donation.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationDetailService {

    @Autowired
    private DonationDetailRepository donationDetailRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public DonationDetailDTO getById(Long id) {
        DonationDetail detail = donationDetailRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy thông tin hiến máu"));
        return convertToDTO(detail);
    }

    public List<DonationDetailDTO> getAll() {
        return donationDetailRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DonationDetailDTO create(CreateDonationDetailDTO dto) {
        DonationDetail detail = new DonationDetail();

        detail.setDonAmount(dto.getDonAmount());
        detail.setDonDate(dto.getDonDate());
        detail.setTypeBlood(dto.getBloodType());

        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy lịch hẹn"));
        detail.setAppointment(appointment);

        User staff = userRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy nhân viên"));
        detail.setStaff(staff);

        User member = userRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người hiến máu"));
        detail.setMember(member);

        return convertToDTO(donationDetailRepository.save(detail));
    }

    @Transactional
    public DonationDetailDTO update(Long id, CreateDonationDetailDTO dto) {
        DonationDetail detail = donationDetailRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy thông tin hiến máu"));

        detail.setDonAmount(dto.getDonAmount());
        detail.setDonDate(dto.getDonDate());
        detail.setTypeBlood(dto.getBloodType());

        detail.setAppointment(appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy lịch hẹn")));

        detail.setStaff(userRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy nhân viên")));

        detail.setMember(userRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người hiến máu")));

        return convertToDTO(donationDetailRepository.save(detail));
    }

    @Transactional
    public void delete(Long id) {
        if (!donationDetailRepository.existsById(id)) {
            throw new BadRequestException("Không tìm thấy thông tin hiến máu");
        }
        donationDetailRepository.deleteById(id);
    }

    public DonationDetailDTO getByAppointmentId(Long appointmentId) {
        DonationDetail detail = donationDetailRepository.findByAppointment_Id(appointmentId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy thông tin theo lịch hẹn"));
        return convertToDTO(detail);
    }

    private DonationDetailDTO convertToDTO(DonationDetail detail) {
        DonationDetailDTO dto = new DonationDetailDTO();
        dto.setDonID(detail.getDonID());
        dto.setDonAmount(detail.getDonAmount());
        dto.setDonDate(detail.getDonDate());
        dto.setBloodType(detail.getTypeBlood());
        dto.setAppointmentId(detail.getAppointment().getId());
        dto.setStaffId(detail.getStaff().getId());
        dto.setMemberId(detail.getMember().getId());
        return dto;
    }
}
