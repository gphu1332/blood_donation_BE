package com.example.blood_donation.entity;

import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class BloodInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bloodInvID;
    private int volume;

    private Date importDate;
    private Date exDate;

    //2 khoa ngoai xu ly sau
    private String adID;
    private String doID;

    @Enumerated(EnumType.STRING)
    private TypeBlood typeBlood;
}
