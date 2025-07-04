package com.example.blood_donation.service;

import com.example.blood_donation.dto.LocationDTO;
import com.example.blood_donation.entity.Location;
import com.example.blood_donation.repositoty.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(location -> modelMapper.map(location, LocationDTO.class))
                .collect(Collectors.toList());
    }

    public LocationDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        return modelMapper.map(location, LocationDTO.class);
    }

    public LocationDTO createLocation(LocationDTO dto) {
        Location location = modelMapper.map(dto, Location.class);
        return modelMapper.map(locationRepository.save(location), LocationDTO.class);
    }

    public LocationDTO updateLocation(Long id, LocationDTO dto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        location.setName(dto.getName());
        return modelMapper.map(locationRepository.save(location), LocationDTO.class);
    }

    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
}
