package com.example.blood_donation.service;

import com.example.blood_donation.dto.AdressDTO;
import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.repositoty.AdressRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdressService {

    @Autowired
    private AdressRepository adressRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<AdressDTO> getAll() {
        return adressRepository.findAll().stream()
                .map(adress -> modelMapper.map(adress, AdressDTO.class))
                .toList();
    }

    public AdressDTO getById(Long id) {
        Adress adress = adressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        return modelMapper.map(adress, AdressDTO.class);
    }

    public AdressDTO create(AdressDTO dto) {
        Adress adress = modelMapper.map(dto, Adress.class);
        return modelMapper.map(adressRepository.save(adress), AdressDTO.class);
    }

    public AdressDTO update(Long id, AdressDTO dto) {
        Adress existing = adressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        existing.setName(dto.getName());
        existing.setLatitude(dto.getLatitude());
        existing.setLongitude(dto.getLongitude());

        return modelMapper.map(adressRepository.save(existing), AdressDTO.class);
    }

    public void delete(Long id) {
        adressRepository.deleteById(id);
    }
}
