package com.example.blood_donation.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class SlotResponse {
    private Long slotID;
    private String label;
    private LocalTime start;
    private LocalTime end;
}

