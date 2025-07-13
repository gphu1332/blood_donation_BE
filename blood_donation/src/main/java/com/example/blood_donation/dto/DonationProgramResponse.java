package com.example.blood_donation.dto;

import com.example.blood_donation.enums.ProgramStatus;
import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DonationProgramResponse {
    private Long id;
    private String proName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate dateCreated;


    private Long addressId;

    private Long cityId;
    private String contact;
    private String imageUrl;
    private String description;
    private List<TypeBlood> typeBloods;
    private Long adminId;

    private List<Long> slotIds;

    private ProgramStatus status;

}
