package com.example.blood_donation.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnswerRequest {
    private Long questionId;
    private List<Long> selectedOptionIds;
    private String additionalText;
}
