package com.example.blood_donation.dto;

import lombok.Data;
import java.util.List;

@Data
public class DonationProgramDTO {
    private Long id;
    private String proName;
    private List<Long> slotIds;
    private Long locationId;
//    Người tạo chương trình (nếu cần)
//    private Long adminId;
}
