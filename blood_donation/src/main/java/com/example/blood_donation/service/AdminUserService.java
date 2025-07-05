package com.example.blood_donation.service;

import com.example.blood_donation.dto.AdminUserDTO;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.AdminUserRepository;
import com.example.blood_donation.repositoty.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminUserService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<User> getAllUsers() {
        List<Role> allowedRoles = List.of(Role.ADMIN, Role.STAFF, Role.HOSPITAL_STAFF);
        return adminUserRepository.findByRoleIn(allowedRoles);
    }

    public Optional<User> getUserById(Long id) {
        return adminUserRepository.findById(id);
    }

    public User createUser(User user) {
        return adminUserRepository.save(user);
    }

    public UserDTO updateUserByAdmin(Long id, AdminUserDTO adminDTO) {
        User user = adminUserRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        user.setUsername(adminDTO.getUsername());
        user.setFullName(adminDTO.getFullName());
        user.setEmail(adminDTO.getEmail());
        user.setPhone(adminDTO.getPhone());
        user.setAddress(adminDTO.getAddress());
        user.setCccd(adminDTO.getCccd());
        user.setGender(adminDTO.getGender());
        user.setTypeBlood(adminDTO.getTypeBlood());
        user.setRole(adminDTO.getRole());

        if (adminDTO.getPassword() != null && !adminDTO.getPassword().isBlank()) {
            String hashedPassword = passwordEncoder.encode(adminDTO.getPassword());
            user.setPassword(hashedPassword);
        }

        User updatedUser = adminUserRepository.save(user);

        return modelMapper.map(updatedUser, UserDTO.class);
    }


    public void deleteUser(Long id) {
        adminUserRepository.deleteById(id);
    }
}

