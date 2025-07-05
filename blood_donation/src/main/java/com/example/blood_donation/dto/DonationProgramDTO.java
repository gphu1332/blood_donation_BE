package com.example.blood_donation.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DonationProgramDTO {
    private Long id;
    private String proName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String address;
    private Long locationId;
    private List<Long> slotIds;
    private Long adminId;
}

