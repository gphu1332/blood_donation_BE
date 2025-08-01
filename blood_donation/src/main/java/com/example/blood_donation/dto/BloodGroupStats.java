package com.example.blood_donation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodGroupStats {
    private int quantity200ml;
    private int quantity350ml;
    private int quantity500ml;
    private int totalBags;
    private int totalVolume;
}
