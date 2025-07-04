package com.example.blood_donation.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DonationProgramDTO {
    private Long id;
    private String proName;
    private List<Long> slotIds;
    private Long locationId;
    private String address;
    private LocalDate endDate;
    private LocalDate startDate;
    private Long adminId;
}

