package com.example.blood_donation.dto;

import lombok.Data;

@Data
public class HospitalDTO {
    private Long id;
    private String name;
    private Long adressId;
    private String adressName;
    private Double latitude;
    private Double longitude;
}
