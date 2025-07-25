package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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

    // Dùng LONGTEXT để lưu base64 (nếu dùng base64 ảnh)
    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "StaID")
    private User staff;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Transient // Không lưu DB, dùng để nhận ảnh từ FE
    private MultipartFile file;

    // Optional: Nếu muốn gọi setImageBase64() trong service thì thêm method này
    public void setImageBase64(String base64String) {
        this.imageUrl = base64String;
    }
}
