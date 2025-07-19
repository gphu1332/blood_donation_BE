package com.example.blood_donation.service;

import com.example.blood_donation.dto.CreateDonationDetailDTO;
import com.example.blood_donation.dto.DonationDetailDTO;
import com.example.blood_donation.entity.DonationDetail;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationDetailService {
    @Autowired
    DonationDetailRepository donationDetailRepository;
    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StaffRepository staffRepository;

    public DonationDetailDTO create(CreateDonationDetailDTO dto) {
        DonationDetail donation = new DonationDetail();
        donation.setDonAmount(dto.getDonAmount());
        donation.setDonDate(dto.getDonDate());
        donation.setTypeBlood(dto.getBloodType());
        donation.setAppointment(appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found")));
        User member = memberRepository.findById(dto.getMemberId())
                        .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        if (member.getRole() != Role.MEMBER) {
            throw  new IllegalArgumentException("User is not a member");
        }
        donation.setMember(member);
        donation.setStaff(staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new EntityNotFoundException("Staff not found")));
        return mapToDTO(donationDetailRepository.save(donation));
    }
    public DonationDetailDTO getById(Long id) {
        DonationDetail d = donationDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));
        return mapToDTO(d);
    }
    public List<DonationDetailDTO> getAll() {
        return donationDetailRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public DonationDetailDTO update(Long id, CreateDonationDetailDTO dto) {
        DonationDetail donation = donationDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));
        donation.setDonAmount(dto.getDonAmount());
        donation.setDonDate(dto.getDonDate());
        donation.setTypeBlood(dto.getBloodType());
        donation.setAppointment(appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found")));
        User member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        if (member.getRole() != Role.MEMBER) {
            throw  new IllegalArgumentException("User is not a member");
        }
        donation.setMember(member);
        donation.setStaff(staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new EntityNotFoundException("Staff not found")));
        return mapToDTO(donationDetailRepository.save(donation));
    }
    public void delete(Long id) {
        DonationDetail d = donationDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));
        donationDetailRepository.delete(d);
    }
    private DonationDetailDTO mapToDTO(DonationDetail detail) {
        DonationDetailDTO dto = new DonationDetailDTO();
        dto.setDonID(detail.getDonID());
        dto.setDonAmount(detail.getDonAmount());
        dto.setDonDate(detail.getDonDate());
        dto.setBloodType(detail.getTypeBlood());
        dto.setAppointmentId(detail.getAppointment().getId());
        dto.setMemberId(detail.getMember().getId());
        dto.setStaffId(detail.getStaff().getId());
        return dto;
    }

    //Kim
    public DonationDetailDTO getByAppointmentId(Long appointmentId) {
        DonationDetail detail = donationDetailRepository.findByAppointment_Id(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Donation detail not found for appointment ID: " + appointmentId));
        return mapToDTO(detail);
    }
}
