package com.example.blood_donation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageDTO {
    private String fullName;
    private String email;
    private String message;
}
