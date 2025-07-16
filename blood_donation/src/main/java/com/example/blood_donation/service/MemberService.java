package com.example.blood_donation.service;

import com.example.blood_donation.dto.CreateUpdateMemberRequest;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Lấy tất cả MEMBER
    public List<User> getAllMemberUsers() {
        return memberRepository.findByRole(Role.MEMBER);
    }

    // Lấy 1 MEMBER
    public Optional<User> getMemberUserById(Long id) {
        return memberRepository.findById(id)
                .filter(user -> user.getRole() == Role.MEMBER);
    }

    // Xóa MEMBER
    public void deleteUser(Long id) {
        memberRepository.deleteById(id);
    }

    // Tạo mới MEMBER
    public User createMember(CreateUpdateMemberRequest dto) {
        User user = modelMapper.map(dto, User.class);
        user.setRole(Role.MEMBER);

        // Gán địa chỉ nếu có
        if (dto.getAddress() != null) {
            Adress address = new Adress();
            address.setName(dto.getAddress().getName());
            address.setLatitude(dto.getAddress().getLatitude());
            address.setLongitude(dto.getAddress().getLongitude());
            user.setAddress(address);
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            throw new BadRequestException("Password must not be empty");
        }

        return memberRepository.save(user);
    }

    // Cập nhật MEMBER
    public User updateMember(Long id, CreateUpdateMemberRequest dto) {
        User user = getMemberUserById(id)
                .orElseThrow(() -> new BadRequestException("User not found or not MEMBER"));

        user.setUsername(dto.getUsername());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setCccd(dto.getCccd());
        user.setBirthdate(dto.getBirthdate());
        user.setTypeBlood(dto.getTypeBlood());
        user.setGender(dto.getGender());

        // Cập nhật địa chỉ
        if (dto.getAddress() != null) {
            if (user.getAddress() == null) {
                user.setAddress(new Adress());
            }
            user.getAddress().setName(dto.getAddress().getName());
            user.getAddress().setLatitude(dto.getAddress().getLatitude());
            user.getAddress().setLongitude(dto.getAddress().getLongitude());
        }

        // Nếu người dùng nhập mật khẩu mới
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return memberRepository.save(user);
    }

    public UserDTO mapToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
