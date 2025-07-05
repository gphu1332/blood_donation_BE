package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AnswerOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;

    @Column(length = 500)
    private String additionalText;
}



