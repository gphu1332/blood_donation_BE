package com.example.blood_donation.service;

import com.example.blood_donation.dto.NotificationRequest;
import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.entity.Notification;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.exception.exceptons.BadRequestException;
import com.example.blood_donation.repositoty.AdressRepository;
import com.example.blood_donation.repositoty.NotificationRepository;
import com.example.blood_donation.repositoty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public List<Notification> getNotificationsByCurrentUSer() {
        User user = authenticationService.getCurrentUser();
        return notificationRepository.findByUser(user);
    }

    public Notification updateNotification(Long id,  NotificationRequest request) {
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

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy thông báo với ID: " + id));

        notification.setRead(true); // hoặc setIsRead nếu bạn đã đổi tên
        return notificationRepository.save(notification);
    }

    public List<Notification> getFilteredNotifications(Long userId, LocalDateTime fromDate, LocalDateTime toDate) {
        return notificationRepository.findByFilters(userId, fromDate, toDate);
    }

    public double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

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
                notification.setMessage("Yêu cầu bạn có thể tới địa điểm" + adress.getName() + "để truyền máu được không !!!");
                notification.setUser(user);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setRead(false);

                notificationRepository.save(notification);
            }
        }
    }

}
