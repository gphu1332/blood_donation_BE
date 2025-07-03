package com.example.blood_donation.service;

import com.example.blood_donation.dto.AdminUserDTO;
import com.example.blood_donation.dto.AdminUserResponseDTO;
import com.example.blood_donation.dto.CreateAdminUserDTO;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.AdminUserRepository;
import com.example.blood_donation.repositoty.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminUserService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return adminUserRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return adminUserRepository.findById(id);
    }

    public Long findUserIdByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new BadRequestException("User not found"))
                .getUserID();
    }


    public AdminUserResponseDTO createUserByAdmin(CreateAdminUserDTO dto) {
        // Map DTO -> Entity
        User user = modelMapper.map(dto, User.class);

        // Hash password
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(hashedPassword);

        // Lưu DB
        User saved = adminUserRepository.save(user);

        // Map Entity -> ResponseDTO
        return modelMapper.map(saved, AdminUserResponseDTO.class);
    }

    public UserDTO updateUserByAdmin(Long id, AdminUserDTO adminDTO) {
        User user = adminUserRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        user.setFullName(adminDTO.getFullName());
        user.setEmail(adminDTO.getEmail());
        user.setPhone(adminDTO.getPhone());
        user.setAddress(adminDTO.getAddress());
        user.setCccd(adminDTO.getCccd());
        user.setGender(adminDTO.getGender());
        user.setTypeBlood(adminDTO.getTypeBlood());
        user.setRole(adminDTO.getRole());

        User updatedUser = adminUserRepository.save(user);

        return modelMapper.map(updatedUser, UserDTO.class);
    }

    public void deleteUser(Long id) {
        adminUserRepository.deleteById(id);
    }
}

