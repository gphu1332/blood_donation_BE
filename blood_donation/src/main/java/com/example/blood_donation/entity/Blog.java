package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "content")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contId;

    private String contTitle;
    private String contType;

    @Column(columnDefinition = "TEXT")
    private String contBody;

    private LocalDate conPubDate;

    // Mỗi nhân viên có the quản lý nhiều blogs
    @ManyToOne
    @JoinColumn(name = "StaID")
    private Staff staff;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}
