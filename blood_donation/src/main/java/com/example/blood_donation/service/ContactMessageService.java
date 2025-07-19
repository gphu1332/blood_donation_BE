package com.example.blood_donation.service;

import com.example.blood_donation.dto.ContactMessageDTO;
import com.example.blood_donation.entity.ContactMessage;
import com.example.blood_donation.repository.ContactMessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    public void saveMessage(ContactMessageDTO dto) {
        ContactMessage message = modelMapper.map(dto, ContactMessage.class);
        message.setCreatedAt(LocalDateTime.now());
        contactMessageRepository.save(message);

        try {
            emailService.sendContactMessageToAdmin(dto.getFullName(), dto.getEmail(), dto.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

