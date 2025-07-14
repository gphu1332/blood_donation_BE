package com.example.blood_donation.service;

import com.example.blood_donation.dto.ResetPasswordRequest;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ResetPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Lưu OTP tạm thời
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    // Lưu trạng thái xác minh OTP
    private final Map<String, Boolean> verifiedOtpEmails = new ConcurrentHashMap<>();

    // Scheduler để xóa OTP sau một thời gian
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Gửi mã OTP đến email
     */
    public String generateOtp(String email) {
        String otp = String.valueOf(new java.util.Random().nextInt(900000) + 100000);
        otpStorage.put(email, otp);

        // Sau 1 phút sẽ tự động xóa OTP
        scheduler.schedule(() -> otpStorage.remove(email), 1, TimeUnit.MINUTES);

        emailService.sendOtpEmail(email, otp);

        return otp;
    }

    /**
     * Xác minh mã OTP, nếu đúng thì trả về token và đánh dấu email đã xác minh
     */
    public String verifyOtp(String email, String otp) {
        boolean isValid = otp.equals(otpStorage.get(email));

        if (!isValid) {
            throw new BadRequestException("Mã OTP không hợp lệ hoặc đã hết hạn");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng"));

        // Xác minh thành công thì lưu trạng thái
        verifiedOtpEmails.put(email, true);

        return tokenService.generateToken(user);
    }

    /**
     * Đặt lại mật khẩu mới sau khi đã xác minh OTP
     */
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        User user = authenticationService.getCurrentUser();
        String email = user.getEmail();

        // Kiểm tra đã xác minh OTP chưa
        if (!verifiedOtpEmails.getOrDefault(email, false)) {
            throw new BadRequestException("Email chưa được xác minh OTP");
        }

        // Cập nhật mật khẩu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Dọn dẹp trạng thái sau khi đặt lại mật khẩu thành công
        verifiedOtpEmails.remove(email);
        otpStorage.remove(email); // Phòng trường hợp chưa bị xoá tự động
    }
}
