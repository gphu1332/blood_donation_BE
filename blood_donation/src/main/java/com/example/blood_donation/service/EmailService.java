package com.example.blood_donation.service;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Gửi email OTP xác minh
     */
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            // 1. Gắn biến vào template
            Context context = new Context();
            context.setVariable("name", toEmail);
            context.setVariable("otp", otp);
            context.setVariable("button", "Xác minh ngay");
            context.setVariable("link", "https://your-domain.com/verify?email=" + toEmail + "&otp=" + otp);

            // 2. Load nội dung email từ HTML
            String html = templateEngine.process("emailtemplate.html", context);

            // 3. Thiết lập nội dung email
            helper.setTo(toEmail);
            helper.setSubject("🔐 Mã OTP xác thực tài khoản - Blood Donation");
            helper.setText(html, true); // true = HTML

            // Thêm ảnh nội tuyến (logo)
            File logoFile = new ClassPathResource("static/assets/img.png").getFile();
            helper.addInline("logoImage", logoFile);
;

            // 5. Gửi mail
            mailSender.send(message);

        } catch (MessagingException | IOException e) {
            throw new RuntimeException("Lỗi khi gửi email OTP", e);
        }
    }

    public void sendAppointmentReminderEmail(String toEmail, String name, LocalDateTime appointmentTime) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("📅 Nhắc nhở lịch hiến máu");

            String html = """
            <div style="font-family: Arial; padding: 20px;">
                <h2>Xin chào %s,</h2>
                <p>Bạn có một lịch hẹn hiến máu vào <strong>%s</strong>.</p>
                <p>Hãy đảm bảo sức khỏe và đến đúng giờ nhé!</p>
                <p style="margin-top: 20px;">Trân trọng,<br>Blood Donation System</p>
            </div>
        """.formatted(name, appointmentTime.toLocalDate().toString());

            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không gửi được email nhắc lịch", e);
        }
    }


}
