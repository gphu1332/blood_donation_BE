package com.example.blood_donation.service;

import com.example.blood_donation.dto.LocationDTO;
import com.example.blood_donation.entity.City;
import com.example.blood_donation.repositoty.CityRepository;
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

    public List<LocationDTO> getAllLocations() {
        return cityRepository.findAll().stream()
                .map(city -> modelMapper.map(city, LocationDTO.class))
                .collect(Collectors.toList());
    }

    public LocationDTO getLocationById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        return modelMapper.map(city, LocationDTO.class);
    }

    public LocationDTO createLocation(LocationDTO dto) {
        City city = modelMapper.map(dto, City.class);
        return modelMapper.map(cityRepository.save(city), LocationDTO.class);
    }

    public LocationDTO updateLocation(Long id, LocationDTO dto) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        city.setName(dto.getName());
        return modelMapper.map(cityRepository.save(city), LocationDTO.class);
    }

    public void deleteLocation(Long id) {
        cityRepository.deleteById(id);
    }
}
