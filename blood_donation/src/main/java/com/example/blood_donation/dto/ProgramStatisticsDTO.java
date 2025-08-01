package com.example.blood_donation.dto;

import com.example.blood_donation.enums.TypeBlood;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramStatisticsDTO {
    private Long programId;
    private long totalAppointments;

    private int cancelledCount;
    private int rejectedCount;
    private int approvedCount;
    private int pendingCount;
    private int fulfilledCount;

    private double successRate; // ti le don fulfilled
    private double failRate; // ti le don cancel va reject

    private int totalBloodBagsCollected;
    private int totalVolumeCollected;

    private Map<TypeBlood, BloodGroupStats> bloodStats;
}
