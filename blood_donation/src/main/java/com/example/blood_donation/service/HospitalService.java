package com.example.blood_donation.service;

import com.example.blood_donation.dto.HospitalDTO;
import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.entity.Hospital;
import com.example.blood_donation.repositoty.AdressRepository;
import com.example.blood_donation.repositoty.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private AdressRepository adressRepository;

    public List<HospitalDTO> getAll() {
        return hospitalRepository.findAll().stream().map(this::toDTO).toList();
    }

    public HospitalDTO getById(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
        return toDTO(hospital);
    }

    public HospitalDTO create(HospitalDTO dto) {
        Hospital hospital = new Hospital();
        hospital.setName(dto.getName());

        if (dto.getAdressId()!= null) {
            Adress adress = adressRepository.findById(dto.getAdressId())
                    .orElseThrow(() -> new RuntimeException("Adress not found"));
            hospital.setAdress(adress);
        }
        return toDTO(hospitalRepository.save(hospital));
    }

    public HospitalDTO update(Long id, HospitalDTO dto) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
        hospital.setName(dto.getName());
        if (dto.getAdressId() != null) {
            Adress adress = adressRepository.findById(dto.getAdressId())
                    .orElseThrow(() -> new RuntimeException("Adress not found"));
            hospital.setAdress(adress);
        } else {
            hospital.setAdress(null);
        }
        return toDTO(hospitalRepository.save(hospital));
    }

    public void delete(Long id) {
        hospitalRepository.deleteById(id);
    }

    private HospitalDTO toDTO(Hospital hospital) {
        HospitalDTO dto = new HospitalDTO();
        dto.setId(hospital.getId());
        dto.setName(hospital.getName());

        if (hospital.getAdress() != null) {
            dto.setAdressId(hospital.getAdress().getId());
            dto.setAdressName(hospital.getAdress().getName());
            dto.setLatitude(hospital.getAdress().getLatitude());
            dto.setLongitude(hospital.getAdress().getLongitude());
        }

        return dto;
    }
}
