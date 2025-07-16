package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByContType(String contType);
    List<Blog> findByIsDeletedFalse();
}
