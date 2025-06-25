package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Status;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class BloodRequestDTO {
    private LocalDate reqCreatedDate;
    private String isEmergency;
    private Status reqStatus;
    private Long staID;
    private Long medID;
    private List<BloodRequestDetailDTO> details;
}
