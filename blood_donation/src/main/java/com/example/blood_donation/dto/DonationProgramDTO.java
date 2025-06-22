package com.example.blood_donation.dto;


import lombok.Data;

@Data
public class DonationProgramDTO {
    private Long id;
    private String proName;
    private Long slotId;
    private Long locationId;
}

