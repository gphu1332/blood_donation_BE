package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    // Nếu người dùng chỉ nhập text (TEXT question)
    @Column(length = 500)
    private String answerText;
}
