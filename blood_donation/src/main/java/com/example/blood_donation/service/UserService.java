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
        // ✅ Tìm user chưa bị xóa
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BadRequestException("User not found or has been deleted"));

        // ✅ Kiểm tra trùng thông tin
        if (userRepository.existsByUsernameAndIdNotAndDeletedFalse(userDTO.getUsername(), id)) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmailAndIdNotAndDeletedFalse(userDTO.getEmail(), id)) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.existsByCccdAndIdNotAndDeletedFalse(userDTO.getCccd(), id)) {
            throw new BadRequestException("CCCD already exists");
        }

        // ✅ Cập nhật thông tin cơ bản
        user.setUsername(userDTO.getUsername());
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setBirthdate(userDTO.getBirthdate());
        user.setCccd(userDTO.getCccd());
        user.setGender(userDTO.getGender());
        user.setTypeBlood(userDTO.getTypeBlood());

        // ✅ Xử lý địa chỉ
        AdressDTO addressDTO = userDTO.getAddress();
        if (addressDTO != null) {
            Adress address;

            if (addressDTO.getId() != null) {
                // Trường hợp có ID → tìm theo ID
                System.out.println("AddressDTO ID = " + addressDTO.getId());
                address = adressRepository.findById(addressDTO.getId())
                        .orElseThrow(() -> new BadRequestException("Address not found"));
            } else {
                // Trường hợp không có ID → tìm theo name + lat + lng
                Optional<Adress> existingAddress = adressRepository.findByNameAndLatitudeAndLongitude(
                        addressDTO.getName(),
                        addressDTO.getLatitude(),
                        addressDTO.getLongitude()
                );

                if (existingAddress.isPresent()) {
                    address = existingAddress.get();
                } else {
                    Adress newAddress = new Adress();
                    newAddress.setName(addressDTO.getName());
                    newAddress.setLatitude(addressDTO.getLatitude());
                    newAddress.setLongitude(addressDTO.getLongitude());
                    adressRepository.saveAndFlush(newAddress); // đảm bảo nó được commit ngay
                    address = newAddress;
                }
            }

            user.setAddress(address); // Gán địa chỉ dùng chung
        }

        // ✅ Lưu và trả về DTO
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }
}
