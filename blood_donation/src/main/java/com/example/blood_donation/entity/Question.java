package com.example.blood_donation.entity;

import com.example.blood_donation.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Enumerated(EnumType.STRING)
    private QuestionType type; // SINGLE_SELECT, MULTI_SELECT, TEXT
}

