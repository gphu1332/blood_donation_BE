package com.example.blood_donation.service;

import com.example.blood_donation.entity.Blog;
import com.example.blood_donation.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    private final String uploadDir = "uploads/images";

    public Blog create(Blog blog) {
        blog.setIsDeleted(false);
        blog.setConPubDate(LocalDate.now());

        if (blog.getFile() != null && !blog.getFile().isEmpty()) {
            try {
                byte[] bytes = blog.getFile().getBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                blog.setImageBase64(base64);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi đọc file ảnh", e);
            }
        }

        return blogRepository.save(blog);
    }

    public List<Blog> getAll() {
        return blogRepository.findByIsDeletedFalse();
    }

    public Blog getById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Blog"));
        if (blog.getIsDeleted()) throw new RuntimeException("Blog đã bị xóa");
        return blog;
    }

    public Blog update(Long id, Blog updated) {
        Blog blog = getById(id);
        blog.setContTitle(updated.getContTitle());
        blog.setContType(updated.getContType());
        blog.setContBody(updated.getContBody());

        if (updated.getFile() != null && !updated.getFile().isEmpty()) {
            try {
                byte[] bytes = updated.getFile().getBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                blog.setImageBase64(base64);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi đọc file ảnh", e);
            }
        }

        return blogRepository.save(blog);
    }

    public void delete(Long id) {
        Blog blog = getById(id);
        blog.setIsDeleted(true);
        blogRepository.save(blog);
    }
}