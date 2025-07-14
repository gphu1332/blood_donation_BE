package com.example.blood_donation.service;

import com.example.blood_donation.dto.*;
import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.entity.Hospital;
import com.example.blood_donation.entity.MedicalStaff;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.AdminUserRepository;
import com.example.blood_donation.repositoty.HospitalRepository;
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

    @Autowired
    private HospitalRepository hospitalRepository;

    public List<User> getAllUsers() {
        List<Role> allowedRoles = List.of(Role.ADMIN, Role.STAFF, Role.HOSPITAL_STAFF);
        return adminUserRepository.findByRoleIn(allowedRoles);
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

        // Nếu là HOSPITAL_STAFF -> ép kiểu và gán hospital
        if(dto.getRole() == Role.HOSPITAL_STAFF && dto.getHospitalId() != null) {
            if(user instanceof MedicalStaff staff) {
                Hospital hospital = hospitalRepository.findById(dto.getHospitalId())
                        .orElseThrow(() -> new BadRequestException("Hospital not found"));
                staff.setHospital(hospital);
            }
        }

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

        user.setUsername(adminDTO.getUsername());
        user.setFullName(adminDTO.getFullName());
        user.setEmail(adminDTO.getEmail());
        user.setPhone(adminDTO.getPhone());

        if (adminDTO.getAddress() != null) {
            AdressDTO addressDTO = adminDTO.getAddress();
            Adress address = new Adress();
            address.setName(addressDTO.getName());
            address.setLatitude(addressDTO.getLatitude());
            address.setLongitude(addressDTO.getLongitude());
            user.setAddress(address);
        }

        user.setCccd(adminDTO.getCccd());
        user.setGender(adminDTO.getGender());
        user.setTypeBlood(adminDTO.getTypeBlood());
        user.setRole(adminDTO.getRole());
        user.setBirthdate(adminDTO.getBirthdate());

        if (adminDTO.getPassword() != null && !adminDTO.getPassword().isBlank()) {
            String hashedPassword = passwordEncoder.encode(adminDTO.getPassword());
            user.setPassword(hashedPassword);
        }

        // Nếu là Hospital Staff -> set hospital
        if (user instanceof MedicalStaff staff && adminDTO.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(adminDTO.getHospitalId())
                    .orElseThrow(() -> new BadRequestException("Hospital not found"));
            staff.setHospital(hospital);
        }

        User updatedUser = adminUserRepository.save(user);

        return modelMapper.map(updatedUser, UserDTO.class);
    }

    public void deleteUser(Long id) {
        User user = adminUserRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            long adminCount = adminUserRepository.countByRole(Role.ADMIN);

            if (adminCount <= 1) {
                throw new BadRequestException("Không thể xóa tài khoản ADMIN duy nhất còn lại.");
            }
        }

        adminUserRepository.deleteById(id);
    }

}

