package com.example.blood_donation.controller;

import com.example.blood_donation.dto.NotificationFilterRequest;
import com.example.blood_donation.dto.NotificationRequest;
import com.example.blood_donation.entity.Notification;
import com.example.blood_donation.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notification")
@SecurityRequirement(name = "api")
@Tag(name = "Notification", description = "Quản lý thông báo người dùng")
public class NotificationAPI {
    @Autowired
    NotificationService notificationService;

//    @PreAuthorize("hasRole('STAFF')")
    @PostMapping
    @Operation(
            summary = "Tạo thông báo mới",
            description = "Tạo mới một thông báo và gán cho người dùng cụ thể , lưu ý phải role: STAFF"
    )
    public ResponseEntity create(@Valid @RequestBody  NotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

//    @PreAuthorize("hasRole('STAFF')")
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông báo", description = "Cập nhật tiêu đề, nội dung và trạng thái đã đọc của thông báo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thông báo")
    })
    public ResponseEntity<Notification> update(
            @PathVariable Long id,
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }


    @GetMapping("/me")
    @Operation(summary = "Lấy danh sách thông báo của chính người dùng đang đăng nhập")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách thông báo"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ")
    })
    public ResponseEntity<List<Notification>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getNotificationsByCurrentUSer());
    }
    @PutMapping("/{id}/read")
    @Operation(summary = "Đánh dấu đã đọc", description = "Đánh dấu một thông báo là đã đọc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đánh dấu thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thông báo")
    })
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

//        @PreAuthorize("hasRole('STAFF')")
    @GetMapping
    @Operation(summary = "Lấy danh sách thông báo có lọc", description = "Lọc theo userId và khoảng thời gian tạo. Hỗ trợ phân trang và sắp xếp.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy thành công")
    })
    public ResponseEntity getNotifications(
            @ParameterObject @ModelAttribute NotificationFilterRequest filter
    ) {
        List<Notification> result = notificationService.getFilteredNotifications(
                filter.getUserId(), filter.getFromDate(), filter.getToDate()
        );
        return ResponseEntity.ok(result);
    }
    @PostMapping("/urgent")
    @Operation(
            summary = "Gửi thông báo hiến máu khẩn cấp",
            description = "Gửi thông báo đến tất cả người dùng nằm trong bán kính 5km từ địa điểm được chọn. " +
                    "Chỉ nên dùng bởi nhân viên có role: STAFF."
    )
    public ResponseEntity<String> notifyUrgentBloodRequest(@RequestParam Long addressId) {
        notificationService.notifyUsersNearHospitals(addressId);
        return ResponseEntity.ok("Đã gửi thông báo cho những người dùng gần địa điểm.");
    }
}
