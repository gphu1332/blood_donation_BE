package com.example.blood_donation.service;

import com.example.blood_donation.dto.DonationProgramDTO;
import com.example.blood_donation.entity.DonationProgram;
import com.example.blood_donation.repositoty.DonationProgramRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationProgramService {

    @Autowired
    private DonationProgramRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public List<DonationProgramDTO> getAll() {
        return repository.findAll().stream()
                .map(pro -> modelMapper.map(pro, DonationProgramDTO.class))
                .collect(Collectors.toList());
    }

    public DonationProgramDTO getById(Long id) {
        DonationProgram program = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
        return modelMapper.map(program, DonationProgramDTO.class);
    }

    public DonationProgramDTO create(DonationProgramDTO dto) {
        DonationProgram program = modelMapper.map(dto, DonationProgram.class);
        return modelMapper.map(repository.save(program), DonationProgramDTO.class);
    }

    public DonationProgramDTO update(Long id, DonationProgramDTO dto) {
        DonationProgram existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
        modelMapper.map(dto, existing);
        return modelMapper.map(repository.save(existing), DonationProgramDTO.class);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}