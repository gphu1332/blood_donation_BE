package com.example.blood_donation.entity;

import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BloodRequestDetailId implements Serializable {
    @Column(name = "reqid") // ✅ Bắt buộc phải map đúng tên cột
    private Long reqId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_blood") // ✅ Trùng tên column trong bảng
    private TypeBlood typeBlood;
}



