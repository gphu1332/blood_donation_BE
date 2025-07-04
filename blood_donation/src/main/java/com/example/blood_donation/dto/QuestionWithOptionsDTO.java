package com.example.blood_donation.dto;

import com.example.blood_donation.enums.QuestionType;
import lombok.Data;

import java.util.List;

@Data
public class QuestionWithOptionsDTO {
    private Long id;
    private String content;
    private QuestionType type;
    private List<OptionDto> options;

    @Data
    public static class OptionDto {
        private Long id;
        private String label;
        private Boolean requiresText;
    }
}

