package com.example.blood_donation.service;

import com.example.blood_donation.dto.*;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.repository.AppointmentRepository;
import com.example.blood_donation.repository.DonationProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AppointmentRepository appointmentRepository;
    private final DonationProgramRepository donationProgramRepository;

    public Map<String, Object> getAppointmentSummary() {
        long total = appointmentRepository.count();
        long fulfilled = appointmentRepository.countByStatus(Status.FULFILLED);
        long cancelled = appointmentRepository.countByStatus(Status.CANCELLED);

        double percent = total == 0 ? 0 : (fulfilled * 100.0 / total);

        Map<String, Object> map = new HashMap<>();
        map.put("totalAppointments", total);
        map.put("fulfilled", fulfilled);
        map.put("cancelled", cancelled);
        map.put("completedPercentage", percent);

        return map;
    }


    public Map<String, Object> getProgramSummary() {
        long total = donationProgramRepository.count();
        return Map.of("totalPrograms", total);
    }

    public List<AppointmentMonthlyStatsDTO> getAppointmentStatsByMonth(LocalDate start, LocalDate end) {
        List<Object[]> rows = appointmentRepository.countByMonthAndStatus(start, end);
        Map<String, AppointmentMonthlyStatsDTO> result = new HashMap<>();

        for (Object[] row : rows) {
            String date = (String) row[0];
            Status status = Status.valueOf(row[1].toString());
            long count = (Long) row[2];

            result.putIfAbsent(date, new AppointmentMonthlyStatsDTO(date, 0, 0));
            AppointmentMonthlyStatsDTO stat = result.get(date);

            if (status == Status.FULFILLED) stat.setFulfilled(count);
            else if (status == Status.CANCELLED) stat.setCancelled(count);
        }

        return new ArrayList<>(result.values());
    }

    public List<TopUserDTO> getTop10Users() {
        return appointmentRepository.findTop10Users().stream()
                .limit(10)
                .map(row -> new TopUserDTO((String) row[0], (Long) row[1]))
                .toList();
    }
    public List<TopProgramDTO> getTop10Programs() {
        return appointmentRepository.findTop10Programs().stream()
                .limit(10)
                .map(row -> new TopProgramDTO((String) row[0], (Long) row[1]))
                .toList();
    }

    public List<ProgramMonthlyStatsDTO> getProgramStatsByMonth(LocalDate start, LocalDate end) {
        List<Object[]> results = donationProgramRepository.countProgramsByMonth(start, end);

        return results.stream()
                .map(row -> {
                    String monthStr = (String) row[0]; // "2025-07"
                    LocalDate monthDate = LocalDate.parse(monthStr + "-01"); // "2025-07-01"
                    long count = (Long) row[1];
                    return new ProgramMonthlyStatsDTO(monthDate, count);
                })
                .toList();
    }


    public YearSummaryDTO getSummaryByYear(int year) {
        long totalPrograms = donationProgramRepository.countByYear(year);
        long totalAppointments = appointmentRepository.countByYear(year);
        return new YearSummaryDTO(year, totalPrograms, totalAppointments);
    }

    public List<TopProgramDTO> getTopProgramsByYear(int year) {
        return appointmentRepository.findTop10ProgramsByYear(year, PageRequest.of(0, 10))
                .stream()
                .map(row -> new TopProgramDTO((String) row[0], (Long) row[1]))
                .toList();
    }

    public List<TopUserDTO> getTopUsersByYear(int year) {
        return appointmentRepository.findTop10UsersByYear(year, PageRequest.of(0, 10))
                .stream()
                .map(row -> new TopUserDTO((String) row[0], (Long) row[1]))
                .toList();
    }

    public List<Integer> getAvailableYears() {
        return donationProgramRepository.findAvailableYears();
    }

}
