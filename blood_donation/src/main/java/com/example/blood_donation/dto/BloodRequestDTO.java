package com.example.blood_donation.dto;

import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.entity.BloodRequestPriority;
import lombok.Data;

import java.util.List;

@Data
public class BloodRequestDTO {
    private BloodRequest request;
    private List<BloodRequestPriority> priorities;
}
