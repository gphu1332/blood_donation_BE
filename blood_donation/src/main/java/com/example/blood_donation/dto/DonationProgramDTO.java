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
    private Double latitude;
    private Double longitude;
    private String address;
    private List<Long> slotIds;
    private Long locationId;
    private TypeBlood typeBlood;
    private String description;
    private String contact;
    private String imageUrl;
    private Long adminId;
}
