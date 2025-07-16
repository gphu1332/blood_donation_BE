package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByContType(String contType);
    List<Blog> findByIsDeletedFalse();
}
