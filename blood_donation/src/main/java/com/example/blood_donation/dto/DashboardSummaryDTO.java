package com.example.blood_donation.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardSummaryDTO {
    private long totalAppointments;
    private long totalPrograms;
    private double completedPercentage;
    private List<AppointmentMonthlyStatsDTO> appointmentStatsByMonth;
    private List<TopProgramDTO> topPrograms;
    private List<TopUserDTO> topUsers;
}
