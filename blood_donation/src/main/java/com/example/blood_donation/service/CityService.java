package com.example.blood_donation.service;

import com.example.blood_donation.dto.CityDTO;
import com.example.blood_donation.entity.City;
import com.example.blood_donation.repository.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<CityDTO> getAllCitys() {
        return cityRepository.findAll().stream()
                .filter(city -> !city.isDeleted())
                .map(city -> modelMapper.map(city, CityDTO.class))
                .toList();
    }

    public CityDTO getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found"));
        return modelMapper.map(city, CityDTO.class);
    }

    public CityDTO createCity(CityDTO dto) {
        City city = modelMapper.map(dto, City.class);
        return modelMapper.map(cityRepository.save(city), CityDTO.class);
    }

    public CityDTO updateCity(Long id, CityDTO dto) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found"));

        city.setName(dto.getName());
        return modelMapper.map(cityRepository.save(city), CityDTO.class);
    }

    @Transactional
    public void delete(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City not found"));

        city.setDeleted(true);
        cityRepository.save(city);
    }

}
