package com.example.blood_donation.controller;

import com.example.blood_donation.dto.MedicalStaffDTO;
import com.example.blood_donation.entity.Hospital;
import com.example.blood_donation.entity.MedicalStaff;
import com.example.blood_donation.repository.HospitalRepository;
import com.example.blood_donation.repository.MedicalStaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/medical-staffs")
public class MedicalStaffController {

    @Autowired
    private MedicalStaffRepository medicalStaffRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MedicalStaffDTO dto) {
        Optional<Hospital> hospitalOptional = hospitalRepository.findById(dto.getHospitalId());
        if (hospitalOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Hospital not found");
        }

        MedicalStaff staff = new MedicalStaff();
        staff.setFullName(dto.getFullName());
        staff.setBirthdate(dto.getBirthdate());
        staff.setHospital(hospitalOptional.get());

        staff.setUsername(generateUsername(dto.getFullName()));
        staff.setPassword("default_password");

        medicalStaffRepository.save(staff);
        return ResponseEntity.ok("Medical staff created");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MedicalStaffDTO dto) {
        Optional<MedicalStaff> staffOptional = medicalStaffRepository.findById(id);
        if (staffOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<Hospital> hospitalOptional = hospitalRepository.findById(dto.getHospitalId());
        if (hospitalOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Hospital not found");
        }

        MedicalStaff staff = staffOptional.get();
        staff.setFullName(dto.getFullName());
        staff.setBirthdate(dto.getBirthdate());
        staff.setHospital(hospitalOptional.get());

        medicalStaffRepository.save(staff);
        return ResponseEntity.ok("Medical staff updated");
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(medicalStaffRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return medicalStaffRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    private String generateUsername(String fullName) {
        return fullName.toLowerCase().replaceAll(" ", "") + System.currentTimeMillis() % 1000;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!medicalStaffRepository.existsById(id))
            return ResponseEntity.notFound().build();
        medicalStaffRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

}
