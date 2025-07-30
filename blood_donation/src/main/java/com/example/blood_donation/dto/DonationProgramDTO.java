package com.example.blood_donation.dto;

import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DonationProgramDTO {
    private Long id;
    private String proName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate dateCreated;
    private Integer maxParticipant;
    private Long addressId;
    private Long cityId;
    private String description;
    private String contact;
    private String imageUrl;
    private Long adminId;
    private List<Long> slotIds;
    private List<TypeBlood> typeBloods;
}

