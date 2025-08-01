package com.example.blood_donation.service;

import com.example.blood_donation.dto.CityDTO;
import com.example.blood_donation.entity.City;
import com.example.blood_donation.repository.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<CityDTO> getAllLocations() {
        return cityRepository.findAll().stream()
                .map(city -> modelMapper.map(city, CityDTO.class))
                .collect(Collectors.toList());
    }

    public CityDTO getLocationById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        return modelMapper.map(city, CityDTO.class);
    }

    public CityDTO createLocation(CityDTO dto) {
        City city = modelMapper.map(dto, City.class);
        return modelMapper.map(cityRepository.save(city), CityDTO.class);
    }

    public CityDTO updateLocation(Long id, CityDTO dto) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        city.setName(dto.getName());
        return modelMapper.map(cityRepository.save(city), CityDTO.class);
    }

    public void deleteLocation(Long id) {
        cityRepository.deleteById(id);
    }
}
