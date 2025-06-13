package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "content")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contId;
    private String contTitle;
    private String contType;
    private String contBody;
    @ManyToOne
    @JoinColumn(name = "StaID")
    private Staff staff;
}
