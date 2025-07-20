package com.example.blood_donation.service;

import com.example.blood_donation.dto.ResetPasswordRequest;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
public class ResetPasswordService {

    @Autowired private UserRepository userRepository;
    @Autowired private TokenService tokenService;
    @Autowired private EmailService emailService;
    @Autowired private PasswordEncoder passwordEncoder;

    // Lưu OTP và trạng thái xác minh
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, Boolean> verifiedEmails = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * B1: Gửi mã OTP đến email
     */
    public String generateOtp(String email) {
        System.out.println("Bắt đầu xử lý gửi OTP cho email: " + email);

        // 1. Tìm user theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.err.println("Email không tồn tại trong hệ thống: " + email);
                    return new BadRequestException("Email không tồn tại");
                });

        // 2. Sinh mã OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStorage.put(email, otp);
        System.out.println("Mã OTP tạo ra: " + otp + " cho email: " + email);

        // 3. Đặt lịch xoá sau 5 phút
        scheduler.schedule(() -> {
            otpStorage.remove(email);
            System.out.println("🕒 OTP của email " + email + " đã bị xoá sau 5 phút.");
        }, 5, TimeUnit.MINUTES);

        // 4. Gửi email
        try {
            System.out.println("Đang gửi email tới: " + email);
            emailService.sendOtpEmail(email, otp);
            System.out.println("Gửi email OTP thành công cho: " + email);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi OTP cho: " + email);
            e.printStackTrace();
            throw new RuntimeException("Không thể gửi OTP. Chi tiết: " + e.getMessage());
        }

        return "Đã gửi mã OTP đến email.";
    }


    /**
     * B2: Xác minh OTP
     */
    public String verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new BadRequestException("Mã OTP không hợp lệ hoặc đã hết hạn");
        }

        verifiedEmails.put(email, true);
        otpStorage.remove(email);

        return "Xác minh OTP thành công. Bạn có thể đặt lại mật khẩu.";
    }

    /**
     * B3: Đặt lại mật khẩu
     */
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Yêu cầu reset mật khẩu cho email: {}", request.getEmail());

        // Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại"));

        // So khớp mật khẩu xác nhận
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("Mật khẩu xác nhận không khớp cho email: {}", request.getEmail());
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra xác minh OTP bằng email của user
        if (!verifiedEmails.getOrDefault(request.getEmail(), false)) {
            log.warn("Chưa xác minh OTP cho email: {}", request.getEmail());
            throw new BadRequestException("Bạn chưa xác minh OTP");
        }

        // Đặt lại mật khẩu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Dọn dẹp
        verifiedEmails.remove(request.getEmail());

        log.info("Đặt lại mật khẩu thành công cho email: {}", request.getEmail());
    }
}
