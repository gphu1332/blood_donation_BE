package com.example.blood_donation.service;

import com.example.blood_donation.dto.*;
import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.entity.Hospital;
import com.example.blood_donation.entity.MedicalStaff;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repository.AdminUserRepository;
import com.example.blood_donation.repository.HospitalRepository;
import com.example.blood_donation.repository.UserRepository;
import com.example.blood_donation.repository.AdressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminUserService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AdressRepository adressRepository;

    // ✅ Lấy danh sách user chưa bị xóa
    public List<User> getAllUsers() {
        List<Role> allowedRoles = List.of(Role.ADMIN, Role.STAFF, Role.HOSPITAL_STAFF);
        return adminUserRepository.findByRoleInAndDeletedFalse(allowedRoles);
    }

    // ✅ Tìm user chưa bị xóa theo ID
    public Optional<User> getUserById(Long id) {
        return adminUserRepository.findByIdAndDeletedFalse(id);
    }

    // ✅ Tìm ID user theo số điện thoại (chỉ user chưa bị xóa)
    public Long findUserIdByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new BadRequestException("User not found or has been deleted"))
                .getId();
    }

    // ✅ Tạo user mới (ADMIN / STAFF / HOSPITAL_STAFF)
    @Transactional
    public AdminUserResponseDTO createUserByAdmin(CreateAdminUserDTO dto) {
        // Check for duplicates among non-deleted users
        if (userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (dto.getEmail() != null && userRepository.existsByEmailAndDeletedFalse(dto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (dto.getCccd() != null && userRepository.existsByCccdAndDeletedFalse(dto.getCccd())) {
            throw new BadRequestException("CCCD already exists");
        }

        User user = modelMapper.map(dto, User.class);

        // Create and save address first
        if (dto.getAddressName() != null) {
            Adress address = new Adress();
            address.setName(dto.getAddressName());
            address.setLatitude(dto.getLatitude());
            address.setLongitude(dto.getLongitude());
            address = adressRepository.save(address); // Save the address first
            user.setAddress(address);
        }

        // Nếu là HOSPITAL_STAFF thì gán hospital
        if (dto.getRole() == Role.HOSPITAL_STAFF && dto.getHospitalId() != null) {
            if (user instanceof MedicalStaff staff) {
                Hospital hospital = hospitalRepository.findById(dto.getHospitalId())
                        .orElseThrow(() -> new BadRequestException("Hospital not found"));
                staff.setHospital(hospital);
            }
        }

        // Mặc định chưa xóa
        user.setDeleted(false);

        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Lưu
        User saved = adminUserRepository.save(user);
        return modelMapper.map(saved, AdminUserResponseDTO.class);
    }

    // ✅ Cập nhật thông tin user (admin chỉnh sửa)
    @Transactional
    public UserDTO updateUserByAdmin(Long id, AdminUserDTO adminDTO) {
        Optional<User> userOpt = adminUserRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new BadRequestException("User not found");
        }
        User user = userOpt.get();
        if (user.isDeleted()) {
            throw new BadRequestException("User is already deleted");
        }

        user.setUsername(adminDTO.getUsername());
        user.setFullName(adminDTO.getFullName());
        user.setEmail(adminDTO.getEmail());
        user.setPhone(adminDTO.getPhone());

        // Handle address fields individually
        if (adminDTO.getAddressName() != null) {
            Adress address = user.getAddress();
            if (address == null) {
                address = new Adress();
            }
            address.setName(adminDTO.getAddressName());
            address.setLatitude(adminDTO.getLatitude());
            address.setLongitude(adminDTO.getLongitude());
            address = adressRepository.save(address);
            user.setAddress(address);
        }

        user.setCccd(adminDTO.getCccd());
        user.setGender(adminDTO.getGender());
        user.setTypeBlood(adminDTO.getTypeBlood());
        user.setRole(adminDTO.getRole());
        user.setBirthdate(adminDTO.getBirthdate());

        if (adminDTO.getPassword() != null && !adminDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        }

        // Nếu là HOSPITAL_STAFF thì update hospital
        if (user instanceof MedicalStaff staff && adminDTO.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(adminDTO.getHospitalId())
                    .orElseThrow(() -> new BadRequestException("Hospital not found"));
            staff.setHospital(hospital);
        }

        User updatedUser = adminUserRepository.save(user);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    // ✅ XÓA MỀM người dùng
    public void deleteUser(Long id) {
        Optional<User> userOpt = adminUserRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new BadRequestException("User not found");
        }
        User user = userOpt.get();
        if (user.isDeleted()) {
            throw new BadRequestException("User is already deleted");
        }

        if (user.getRole() == Role.ADMIN) {
            long adminCount = adminUserRepository.countByRoleAndDeletedFalse(Role.ADMIN);
            if (adminCount <= 1) {
                throw new BadRequestException("Không thể xóa tài khoản ADMIN duy nhất còn lại.");
            }
        }

        user.setDeleted(true);
        adminUserRepository.save(user);
    }
}
