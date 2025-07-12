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
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;
    @Autowired
    AuthenticationService authenticationService;


    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, Boolean> verifiedOtpEmails = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String generateOtp(String email) {
        String otp = String.valueOf(new java.util.Random().nextInt(900000) + 100000);
        otpStorage.put(email, otp);

        // Lên lịch xóa sau 1 phút
        scheduler.schedule(() -> otpStorage.remove(email), 1, TimeUnit.MINUTES);

        return otp;
    }
    public String verifyOtp(String email, String otp) {
        boolean isValid = otp.equals(otpStorage.get(email));
        String token = "";
        if (isValid) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("User not found"));
           token = tokenService.generateToken(user);
        }
        return token;
    }


    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        User user = authenticationService.getCurrentUser();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


}
