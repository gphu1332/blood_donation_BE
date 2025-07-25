package com.example.blood_donation.service;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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

            // Gắn biến vào template
            Context context = new Context();
            context.setVariable("name", toEmail);
            context.setVariable("otp", otp);
            context.setVariable("button", "Xác minh ngay");
            context.setVariable("link", "https://your-domain.com/verify?email=" + toEmail + "&otp=" + otp);

            // Load nội dung HTML
            String html = templateEngine.process("emailtemplate.html", context);

            helper.setTo(toEmail);
            helper.setSubject("Mã OTP xác thực tài khoản - Blood Donation");
            helper.setText(html, true);



            mailSender.send(message);

        } catch (MessagingException e) {
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
        """.formatted(name, appointmentTime.toLocalDate());

            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không gửi được email nhắc lịch", e);
        }
    }

    public void sendContactMessageToAdmin(String fullName, String fromEmail, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("contact.bloodvn@gmail.com"); // Email admin nhận lời nhắn
            helper.setSubject("📨 Lời nhắn mới từ biểu mẫu liên hệ");

            try {
                helper.setFrom("giaphu123123123@gmail.com", "USER CONTACT!!!");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Không thể thiết lập tên người gửi", e);
            }
            helper.setReplyTo(fromEmail); // ⚠️ Khi admin bấm "Trả lời" sẽ gửi về đúng người

            // Template với Thymeleaf (nếu bạn đã có template engine)
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("email", fromEmail);
            context.setVariable("message", content);

            String html = templateEngine.process("contact-message.html", context);
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Không gửi được email liên hệ", e);
        }
    }


    public void sendSimpleEmail(@Email String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public void sendProgramUpdateEmail(String toEmail, String fullName, String programName,
                                       LocalDate startDate, LocalDate endDate, String location, String note) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("📢 Cập nhật chương trình hiến máu: " + programName);

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("programName", programName);
            context.setVariable("startDate", startDate);
            context.setVariable("endDate", endDate);
            context.setVariable("location", location);
            context.setVariable("note", note);

            String html = templateEngine.process("program-update.html", context);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email cập nhật chương trình", e);
        }
    }

}
