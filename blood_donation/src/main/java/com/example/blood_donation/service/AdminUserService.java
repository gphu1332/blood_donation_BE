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

    @Autowired
    private EmailService emailService;

    // Lấy danh sách user chưa bị xóa
    public List<User> getAllUsers() {
        List<Role> allowedRoles = List.of(Role.ADMIN, Role.STAFF, Role.HOSPITAL_STAFF);
        return adminUserRepository.findByRoleInAndDeletedFalse(allowedRoles);
    }

    // Tìm user chưa bị xóa theo ID
    public Optional<User> getUserById(Long id) {
        return adminUserRepository.findByIdAndDeletedFalse(id);
    }

    // Tìm ID user theo số điện thoại (chỉ user chưa bị xóa)
    public Long findUserIdByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new BadRequestException("User not found or has been deleted"))
                .getId();
    }

    // Tạo nhân viên mới (ADMIN / STAFF / HOSPITAL_STAFF)
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

        User user = mapToEntityFromDTO(dto);

        User saved = adminUserRepository.save(user);

        String title = "Tài khoản của bạn đã được tạo";
        String message = "Xin chào " + saved.getFullName() + ",\n\n"
                + "Tài khoản của bạn đã được tạo bởi quản trị viên hệ thống.\n"
                + "Tên đăng nhập: " + saved.getUsername() + "\n"
                + "Mật khẩu: " + dto.getPassword() + "\n\n"
                + "Vui lòng đăng nhập và báo cáo với bộ phận quản lý để thay đổi mật khẩu.";

        emailService.sendSimpleEmail(saved.getEmail(), title, message);
        // Nếu là MedicalStaff, lưu luôn vào hospitalRepo để đảm bảo cascade
        if (saved instanceof MedicalStaff staff) {
            return mapToDTO(staff);
        }
        return mapToDTO(saved);
    }

    // Cập nhật thông tin nhân viên (admin chỉnh sửa)
    @Transactional
    public UserDTO updateUserByAdmin(Long id, AdminUserDTO adminDTO) {
        Optional<User> userOpt = adminUserRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new BadRequestException("User not found");
        }
        User user = userOpt.get();
        // Chỉ check khi có thay đổi username/email/cccd
        if (!user.getUsername().equals(adminDTO.getUsername())
                && userRepository.existsByUsernameAndDeletedFalse(adminDTO.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (adminDTO.getEmail() != null
                && !adminDTO.getEmail().equals(user.getEmail())
                && userRepository.existsByEmailAndDeletedFalse(adminDTO.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (adminDTO.getCccd() != null
                && !adminDTO.getCccd().equals(user.getCccd())
                && userRepository.existsByCccdAndDeletedFalse(adminDTO.getCccd())) {
            throw new BadRequestException("CCCD already exists");
        }

        if (user.isDeleted()) {
            throw new BadRequestException("User is already deleted");
        }

        // Lưu thông tin gốc để so sánh cho việc gửi email
        String originalEmail = user.getEmail();
        String originalUsername = user.getUsername();
        String originalFullName = user.getFullName();
        String originalPhone = user.getPhone();
        String originalAddress = user.getAddress() != null ? user.getAddress().getName() : null;
        String originalCccd = user.getCccd();
        String originalGender = user.getGender() != null ? user.getGender().toString() : null;
        String originalTypeBlood = user.getTypeBlood() != null ? user.getTypeBlood().toString() : null;
        String originalRole = user.getRole() != null ? user.getRole().toString() : null;
        String originalBirthdate = user.getBirthdate() != null ? user.getBirthdate().toString() : null;
        boolean passwordChanged = adminDTO.getPassword() != null && !adminDTO.getPassword().isBlank();

        user.setUsername(adminDTO.getUsername());
        user.setFullName(adminDTO.getFullName());
        user.setEmail(adminDTO.getEmail());
        user.setPhone(adminDTO.getPhone());

        // Handle address
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

        if (passwordChanged) {
            user.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        }

        // Nếu là HOSPITAL_STAFF thì update hospital
        if (user instanceof MedicalStaff staff && adminDTO.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(adminDTO.getHospitalId())
                    .orElseThrow(() -> new BadRequestException("Hospital not found"));
            staff.setHospital(hospital);
        }

        User updatedUser = adminUserRepository.save(user);

        // Kiểm tra xem thông tin cá nhân có thay đổi không
        String newFullName = updatedUser.getFullName();
        String newPhone = updatedUser.getPhone();
        String newAddress = updatedUser.getAddress() != null ? updatedUser.getAddress().getName() : null;
        String newCccd = updatedUser.getCccd();
        String newGender = updatedUser.getGender() != null ? updatedUser.getGender().toString() : null;
        String newTypeBlood = updatedUser.getTypeBlood() != null ? updatedUser.getTypeBlood().toString() : null;
        String newRole = updatedUser.getRole() != null ? updatedUser.getRole().toString() : null;
        String newBirthdate = updatedUser.getBirthdate() != null ? updatedUser.getBirthdate().toString() : null;

        boolean personalInfoChanged = !java.util.Objects.equals(originalFullName, newFullName) ||
                                    !java.util.Objects.equals(originalPhone, newPhone) ||
                                    !java.util.Objects.equals(originalAddress, newAddress) ||
                                    !java.util.Objects.equals(originalCccd, newCccd) ||
                                    !java.util.Objects.equals(originalGender, newGender) ||
                                    !java.util.Objects.equals(originalTypeBlood, newTypeBlood) ||
                                    !java.util.Objects.equals(originalRole, newRole) ||
                                    !java.util.Objects.equals(originalBirthdate, newBirthdate);

        // Gửi email thông báo cập nhật tài khoản (default)
        String accountTitle = "Thông tin tài khoản của bạn đã được cập nhật";
        StringBuilder accountMessageBuilder = new StringBuilder();
        accountMessageBuilder.append("Xin chào ").append(updatedUser.getFullName()).append(",\n\n")
                .append("Thông tin tài khoản của bạn đã được cập nhật bởi quản trị viên hệ thống.\n\n")
                .append("Thông tin tài khoản hiện tại:\n")
                .append("- Tên đăng nhập: ").append(updatedUser.getUsername()).append("\n")
                .append("- Email: ").append(updatedUser.getEmail()).append("\n");

        if (passwordChanged) {
            accountMessageBuilder.append("- Mật khẩu mới: ").append(adminDTO.getPassword()).append("\n");
        }

        accountMessageBuilder.append("\nVui lòng đăng nhập lại và kiểm tra thông tin. ")
                .append("Nếu bạn có mật khẩu mới, hãy đổi mật khẩu ngay sau khi đăng nhập để đảm bảo an toàn tài khoản.");

        emailService.sendSimpleEmail(updatedUser.getEmail(), accountTitle, accountMessageBuilder.toString());

        // Gửi email thông báo cập nhật thông tin cá nhân nếu có thay đổi
        if (personalInfoChanged) {
            String personalTitle = "Thông tin cá nhân của bạn đã được cập nhật";
            StringBuilder personalMessageBuilder = new StringBuilder();
            personalMessageBuilder.append("Xin chào ").append(updatedUser.getFullName()).append(",\n\n")
                    .append("Thông tin cá nhân của bạn đã được cập nhật bởi quản trị viên hệ thống.\n\n")
                    .append("Thông tin cá nhân hiện tại:\n")
                    .append("- Họ và tên: ").append(updatedUser.getFullName()).append("\n")
                    .append("- Số điện thoại: ").append(updatedUser.getPhone() != null ? updatedUser.getPhone() : "Chưa cập nhật").append("\n")
                    .append("- Địa chỉ: ").append(updatedUser.getAddress() != null ? updatedUser.getAddress().getName() : "Chưa cập nhật").append("\n")
                    .append("- CCCD: ").append(updatedUser.getCccd() != null ? updatedUser.getCccd() : "Chưa cập nhật").append("\n")
                    .append("- Nhóm máu: ").append(updatedUser.getTypeBlood() != null ? updatedUser.getTypeBlood() : "Chưa cập nhật").append("\n")
                    .append("- Giới tính: ").append(updatedUser.getGender() != null ? updatedUser.getGender() : "Chưa cập nhật").append("\n")
                    .append("- Ngày sinh: ").append(updatedUser.getBirthdate() != null ? updatedUser.getBirthdate() : "Chưa cập nhật").append("\n")
                    .append("- Vai trò: ").append(updatedUser.getRole()).append("\n\n")
                    .append("Vui lòng kiểm tra và báo cáo cho bộ phận quản lý nếu có bất kỳ sai sót nào.\n");

            emailService.sendSimpleEmail(updatedUser.getEmail(), personalTitle, personalMessageBuilder.toString());
        }

        // Gửi email thông báo thay đổi địa chỉ email nếu có thay đổi
        if (!originalEmail.equals(updatedUser.getEmail())) {
            String oldEmailTitle = "Địa chỉ email tài khoản của bạn đã được thay đổi";
            String oldEmailMessage = "Xin chào,\n\n"
                    + "Địa chỉ email cho tài khoản '" + originalUsername + "' đã được thay đổi từ "
                    + originalEmail + " thành " + updatedUser.getEmail() + ".\n\n"
                    + "Nếu bạn không yêu cầu thay đổi này, vui lòng liên hệ với bộ phận quản lý ngay lập tức.";

            emailService.sendSimpleEmail(originalEmail, oldEmailTitle, oldEmailMessage);
        }

        return modelMapper.map(updatedUser, UserDTO.class);
    }

    // XÓA MỀM người dùng
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

        // Send email notification before deleting
        String deleteTitle = "Tài khoản của bạn đã bị vô hiệu hóa";
        String deleteMessage = "Xin chào " + user.getFullName() + ",\n\n"
                + "Tài khoản của bạn (Tên đăng nhập: " + user.getUsername() + ") đã bị vô hiệu hóa bởi quản trị viên hệ thống.\n\n"
                + "Lý do: Tài khoản đã bị xóa khỏi hệ thống.\n\n"
                + "Bạn sẽ không thể đăng nhập vào hệ thống với tài khoản này nữa. "
                + "Nếu bạn có thắc mắc hoặc cần hỗ trợ, vui lòng liên hệ với bộ phận quản lý.\n\n";

        emailService.sendSimpleEmail(user.getEmail(), deleteTitle, deleteMessage);

        user.setDeleted(true);
        adminUserRepository.save(user);
    }

    // Hàm map DTO → Entity (tạo mới)
    private User mapToEntityFromDTO(CreateAdminUserDTO dto) {
        User user;

        if (dto.getRole() == Role.HOSPITAL_STAFF) {
            // Tạo MedicalStaff thủ công
            MedicalStaff staff = new MedicalStaff();
            staff.setUsername(dto.getUsername());
            staff.setPassword(passwordEncoder.encode(dto.getPassword()));
            staff.setFullName(dto.getFullName());
            staff.setEmail(dto.getEmail());
            staff.setPhone(dto.getPhone());
            staff.setCccd(dto.getCccd());
            staff.setBirthdate(dto.getBirthdate());
            staff.setGender(dto.getGender());
            staff.setTypeBlood(dto.getTypeBlood());
            staff.setRole(dto.getRole());
            staff.setDeleted(false);

            if (dto.getHospitalId() != null) {
                Hospital hospital = hospitalRepository.findById(dto.getHospitalId())
                        .orElseThrow(() -> new BadRequestException("Hospital not found"));
                staff.setHospital(hospital);
            }

            if (dto.getAddressName() != null) {
                Adress address = new Adress();
                address.setName(dto.getAddressName());
                address.setLatitude(dto.getLatitude());
                address.setLongitude(dto.getLongitude());
                address = adressRepository.save(address);
                staff.setAddress(address);
            }

            user = staff;
        } else {
            user = modelMapper.map(dto, User.class);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setDeleted(false);

            if (dto.getAddressName() != null) {
                Adress address = new Adress();
                address.setName(dto.getAddressName());
                address.setLatitude(dto.getLatitude());
                address.setLongitude(dto.getLongitude());
                address = adressRepository.save(address);
                user.setAddress(address);
            }
        }

        return user;
    }

    // ✅ Hàm map Entity → DTO (trả về phản hồi)
    private AdminUserResponseDTO mapToDTO(User user) {
        return modelMapper.map(user, AdminUserResponseDTO.class);
    }
}
