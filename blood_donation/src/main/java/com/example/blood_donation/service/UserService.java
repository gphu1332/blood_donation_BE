package com.example.blood_donation.service;

import com.example.blood_donation.dto.AdressDTO;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new BadRequestException("User not found");
        }

        User existingUser = optionalUser.get();

        // Kiểm tra username đã tồn tại cho user khác chưa
        if (userRepository.existsByUsernameAndUserIDNot(userDTO.getUsername(), id)) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmailAndUserIDNot(userDTO.getEmail(), id)) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.existsByCccdAndUserIDNot(userDTO.getCccd(), id)) {
            throw new BadRequestException("CCCD already exists");
        }

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setFullName(userDTO.getFullName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhone(userDTO.getPhone());

        // ✅ Chuyển AddressDTO → Adress entity
        AdressDTO addressDTO = userDTO.getAddress();
        if (addressDTO != null) {
            Adress address = new Adress();
            address.setName(addressDTO.getName());
            address.setLatitude(addressDTO.getLatitude());
            address.setLongitude(addressDTO.getLongitude());
            existingUser.setAddress(address);
        }

        existingUser.setBirthdate(userDTO.getBirthdate());
        existingUser.setCccd(userDTO.getCccd());
        existingUser.setGender(userDTO.getGender());
        existingUser.setTypeBlood(userDTO.getTypeBlood());

        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDTO.class);
    }
}
