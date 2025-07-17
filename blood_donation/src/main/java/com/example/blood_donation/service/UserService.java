package com.example.blood_donation.service;

import com.example.blood_donation.dto.AdressDTO;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.AdressRepository;
import com.example.blood_donation.repositoty.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdressRepository adressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BadRequestException("User not found or has been deleted"));

        // Kiểm tra trùng lặp
        if (userRepository.existsByUsernameAndIdNotAndDeletedFalse(userDTO.getUsername(), id)) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmailAndIdNotAndDeletedFalse(userDTO.getEmail(), id)) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.existsByCccdAndIdNotAndDeletedFalse(userDTO.getCccd(), id)) {
            throw new BadRequestException("CCCD already exists");
        }

        // Cập nhật thông tin
        user.setUsername(userDTO.getUsername());
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setBirthdate(userDTO.getBirthdate());
        user.setCccd(userDTO.getCccd());
        user.setGender(userDTO.getGender());
        user.setTypeBlood(userDTO.getTypeBlood());

        // Xử lý địa chỉ
        AdressDTO addressDTO = userDTO.getAddress();
        if (addressDTO != null) {
            Adress address;

            if (addressDTO.getId() != null) {
                // Nếu có id -> tìm theo id
                address = adressRepository.findById(addressDTO.getId())
                        .orElseThrow(() -> new BadRequestException("Address not found"));
            } else {
                // Nếu không có id → tìm theo name để tái sử dụng
                address = adressRepository.findByName(addressDTO.getName())
                        .orElseGet(() -> {
                            Adress newAddress = new Adress();
                            newAddress.setName(addressDTO.getName());
                            newAddress.setLatitude(addressDTO.getLatitude());
                            newAddress.setLongitude(addressDTO.getLongitude());
                            return adressRepository.save(newAddress);
                        });
            }

            user.setAddress(address);
        }

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

}