package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "question_option")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label; // Ví dụ: "Có", "Không", "Khác..."

    private Boolean requiresText; // Nếu chọn option này phải nhập thêm nội dung

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}

