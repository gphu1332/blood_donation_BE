package com.example.blood_donation.controller;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.dto.BloodRequestResponseDTO;
import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.service.BloodRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@SecurityRequirement(name = "api") // Áp dụng bảo mật cho toàn bộ controller
public class BloodRequestAPI {

    @Autowired
    private BloodRequestService service;

    @Operation(summary = "Tạo yêu cầu truyền máu", description = "Medical Staff tạo mới yêu cầu máu, bao gồm danh sách túi máu cần thiết")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo yêu cầu thành công"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ")
    })
    @PostMapping("/hospital")
    public ResponseEntity<BloodRequestResponseDTO> create(
            @Parameter(description = "Thông tin yêu cầu máu")
            @Valid @RequestBody BloodRequestDTO dto) {
        BloodRequest request = service.createRequestFromDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.mapToResponseDTO(request));
    }

    @Operation(summary = "Cập nhật yêu cầu truyền máu", description = "Medical Staff cập nhật nội dung yêu cầu khi trạng thái là PENDING")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy yêu cầu")
    })
    @PutMapping("/hospital/{id}")
    public ResponseEntity<BloodRequestResponseDTO> update(
            @Parameter(description = "ID của yêu cầu")
            @PathVariable Long id,
            @RequestBody BloodRequestDTO dto) {
        BloodRequest request = service.updateRequestByMedical(id, dto);
        return ResponseEntity.ok(service.mapToResponseDTO(request));
    }

    @Operation(summary = "Hủy yêu cầu truyền máu", description = "Medical Staff hủy yêu cầu nếu không còn cần thiết")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hủy thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy yêu cầu")
    })
    @PutMapping("/hospital/{id}/cancel")
    public ResponseEntity<String> cancel(@PathVariable Long id) {
        try {
            service.cancelRequestByMedical(id);
            return ResponseEntity.ok("Yêu cầu đã được hủy thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Lấy danh sách yêu cầu của bệnh viện", description = "Trả về tất cả yêu cầu do Medical Staff thuộc bệnh viện đã tạo")
    @ApiResponse(responseCode = "200", description = "Danh sách yêu cầu")
    @GetMapping("/hospital/{medId}")
    public ResponseEntity<List<BloodRequestResponseDTO>> getByHospital(
            @Parameter(description = "ID của Medical Staff") @PathVariable Long medId) {
        return ResponseEntity.ok(service.getRequestDTOByMedical(medId));
    }

    @Operation(summary = "Duyệt hoặc từ chối yêu cầu", description = "Staff thực hiện hành động 'accept' hoặc 'reject'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công"),
            @ApiResponse(responseCode = "400", description = "Hành động không hợp lệ")
    })
    @PutMapping("/staff/{id}/respond")
    public ResponseEntity<BloodRequestResponseDTO> respond(
            @Parameter(description = "ID yêu cầu") @PathVariable Long id,
            @Parameter(description = "Hành động: accept hoặc reject") @RequestParam String action,
            @Parameter(description = "ID của Staff thực hiện") @RequestParam Long staffId) {
        BloodRequest request = service.respondToRequest(id, action, staffId);
        return ResponseEntity.ok(service.mapToResponseDTO(request));
    }

    @Operation(summary = "Cập nhật trạng thái xử lý yêu cầu", description = "Staff cập nhật trạng thái sang PROCESSING, DONE, CANCELLED...")
    @PutMapping("/staff/{id}/process")
    public ResponseEntity<BloodRequestResponseDTO> process(
            @Parameter(description = "ID yêu cầu") @PathVariable Long id,
            @Parameter(description = "Trạng thái mới") @RequestParam Status status) {
        BloodRequest request = service.updateProcessingStatus(id, status);
        return ResponseEntity.ok(service.mapToResponseDTO(request));
    }

    @Operation(summary = "Lấy danh sách yêu cầu đã xử lý", description = "Trả về tất cả yêu cầu được Staff xử lý")
    @GetMapping("/staff/{staId}")
    public ResponseEntity<List<BloodRequestResponseDTO>> getByStaff(
            @Parameter(description = "ID Staff xử lý") @PathVariable Long staId) {
        return ResponseEntity.ok(
                service.getRequestsByStaff(staId).stream()
                        .map(service::mapToResponseDTO)
                        .toList()
        );
    }

    @Operation(summary = "Lấy tất cả yêu cầu máu (API kiểm thử)")
    @GetMapping("/kimrequests")
    public ResponseEntity<List<BloodRequestResponseDTO>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequestDTOs());
    }

    @Operation(summary = "Xóa mềm yêu cầu truyền máu", description = "Đánh dấu yêu cầu là đã xóa (deleted = true)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @Parameter(description = "ID yêu cầu cần xóa") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lấy yêu cầu máu theo id")
    @GetMapping("/{id}")
    public ResponseEntity<BloodRequestResponseDTO> getRequestById(@PathVariable Long id) {
        BloodRequestResponseDTO dto = service.getRequestDTOById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Medical Staff xác nhận yêu cầu đã hoàn tất", description = "Cập nhật trạng thái yêu cầu thành FULFILLED")
    @PutMapping("/hospital/{id}/fulfill")
    public ResponseEntity<BloodRequestResponseDTO> fulfillRequest(@PathVariable Long id) {
        BloodRequest updated = service.markAsFulfilledByMedical(id);
        return ResponseEntity.ok(service.mapToResponseDTO(updated));
    }


    // Private helper method (không cần annotation Swagger)
    private BloodRequestResponseDTO convertToResponse(BloodRequest req) {
        return service.mapToResponseDTO(req);
    }
}
