package com.example.blood_donation.service;

import com.example.blood_donation.dto.MemberCreateRequest;
import com.example.blood_donation.dto.MemberUpdateRequest;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repository.MemberRepository;
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

    // Lấy danh sách tất cả user có vai trò MEMBER
    public List<UserDTO> getAllMemberUsers() {
        return memberRepository.findByRoleAndDeletedFalse(Role.MEMBER)
                .stream()
                .map(this::mapToUserDTO)
                .toList();
    }

    // Lấy 1 MEMBER theo ID
    public Optional<User> getMemberUserById(Long id) {
        return memberRepository.findByIdAndRoleAndDeletedFalse(id, Role.MEMBER);
    }

    // Xóa mềm MEMBER
    public void deleteUser(Long id) {
        User user = getMemberUserById(id)
                .orElseThrow(() -> new BadRequestException("User not found or not MEMBER"));

        if (user.isDeleted()) {
            throw new BadRequestException("User is already deleted");
        }

        user.setDeleted(true);
        memberRepository.save(user);
    }

    // Tạo mới MEMBER
    public User createMember(MemberCreateRequest dto) {
        // Kiểm tra trùng username, email, cccd
        if (memberRepository.existsByUsernameAndDeletedFalse((dto.getUsername()))){
            throw new BadRequestException("Username đã tồn tại");
        }
        if (memberRepository.existsByEmailAndDeletedFalse(dto.getEmail())) {
            throw new BadRequestException("Email đã tồn tại");
        }
        if (memberRepository.existsByCccdAndDeletedFalse(dto.getCccd())) {
            throw new BadRequestException("CCCD đã tồn tại");
        }

        User user = modelMapper.map(dto, User.class);
        user.setRole(Role.MEMBER);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setDeleted(false); // đảm bảo mặc định là chưa xóa

        // Gán địa chỉ nếu có
        if (dto.getAddress() != null) {
            Adress address = new Adress();
            address.setName(dto.getAddress().getName());
            address.setLatitude(dto.getAddress().getLatitude());
            address.setLongitude(dto.getAddress().getLongitude());
            user.setAddress(address);
        }

        return memberRepository.save(user);
    }

    // Cập nhật MEMBER
    public User updateMember(Long id, MemberUpdateRequest dto) {
        User user = getMemberUserById(id)
                .orElseThrow(() -> new BadRequestException("User not found or not MEMBER"));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setCccd(dto.getCccd());
        user.setBirthdate(dto.getBirthdate());
        user.setTypeBlood(dto.getTypeBlood());
        user.setGender(dto.getGender());

        // Cập nhật địa chỉ nếu có
        if (dto.getAddress() != null) {
            Adress address = user.getAddress() != null ? user.getAddress() : new Adress();
            address.setName(dto.getAddress().getName());
            address.setLatitude(dto.getAddress().getLatitude());
            address.setLongitude(dto.getAddress().getLongitude());
            user.setAddress(address);
        }

        return memberRepository.save(user);
    }

    // Map entity -> DTO
    public UserDTO mapToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
