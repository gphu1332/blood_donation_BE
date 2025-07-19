package com.example.blood_donation.service;

import com.example.blood_donation.dto.NotificationRequest;
import com.example.blood_donation.entity.*;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repository.AdressRepository;
import com.example.blood_donation.repository.AppointmentRepository;
import com.example.blood_donation.repository.NotificationRepository;
import com.example.blood_donation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AdressRepository adressRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Tạo một thông báo mới cho người dùng được chỉ định trong NotificationRequest.
     */
    public Notification createNotification(NotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new  BadRequestException("User không tồn tại"));
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setUser(user);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    /**
     * Lấy danh sách thông báo của chính người dùng đang đăng nhập.
     */
    public List<Notification> getNotificationsByCurrentUSer() {
        User user = authenticationService.getCurrentUser();
        return notificationRepository.findByUser(user);
    }

    /**
     * Cập nhật thông báo theo ID, bao gồm cả nội dung và người nhận.
     */
    public Notification updateNotification(Long id, NotificationRequest request) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy thông báo với ID: " + id));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new  BadRequestException("User không tồn tại"));
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setUser(user);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * Đánh dấu thông báo đã đọc theo ID.
     */
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy thông báo với ID: " + id));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    /**
     * Lấy danh sách thông báo có lọc theo người dùng và khoảng thời gian.
     */
    public List<Notification> getFilteredNotifications(Long userId, LocalDateTime fromDate, LocalDateTime toDate) {
        return notificationRepository.findByFilters(userId, fromDate, toDate);
    }

    /**
     * Hàm hỗ trợ tính khoảng cách giữa 2 tọa độ (lat/lng) theo công thức Haversine.
     */
    public double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Bán kính trái đất (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Gửi thông báo khẩn cấp cho tất cả người dùng nằm trong bán kính 5km quanh một địa chỉ cho trước.
     */
    public void notifyUsersNearHospitals(Long addressId) {
        List<User> users = userRepository.findAll();
        Adress adress = adressRepository.findById(addressId)
                .orElseThrow(() -> new BadRequestException("adress not found"));

        double centerLat = adress.getLatitude();
        double centerLng = adress.getLongitude();
        double radiusKm = 5.0;

        for (User user : users) {
            double distance = calculateDistanceKm(
                    centerLat, centerLng,
                    user.getAddress().getLatitude(), user.getAddress().getLongitude()
            );
            if (distance <= radiusKm) {
                Notification notification = new Notification();
                notification.setTitle("Cần máu khẩn cấp");
                notification.setMessage("Yêu cầu bạn có thể tới địa điểm " + adress.getName() + " để truyền máu được không !!!");
                notification.setUser(user);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setRead(false);

                notificationRepository.save(notification);
            }
        }
    }

    /**
     * Gửi email nhắc nhở đến những người có lịch hiến máu vào ngày mai.
     * Dùng trong method autoSendAppointmentReminderEmails().
     */
    public void notifyUpcomingAppointments() {
        LocalDate targetDate = LocalDate.now().plusDays(1);

        List<Appointment> appointments = appointmentRepository.findAppointmentsForProgramDate(targetDate);

        for (Appointment appointment : appointments) {
            User user = appointment.getUser();
            DonationProgram program = appointment.getProgram();

            emailService.sendAppointmentReminderEmail(
                    user.getEmail(),
                    user.getFullName(),
                    program.getStartDate().atStartOfDay()
            );
        }
    }

    /**
     * Tự động gửi email nhắc lịch hiến máu hàng ngày vào lúc 08:00 sáng.
     * Được cấu hình với @Scheduled(cron = "0 0 8 * * *")
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void autoSendAppointmentReminderEmails() {
        notifyUpcomingAppointments();
    }
}
