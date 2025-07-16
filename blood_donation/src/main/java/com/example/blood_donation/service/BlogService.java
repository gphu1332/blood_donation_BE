package com.example.blood_donation.service;

import com.example.blood_donation.entity.Blog;
import com.example.blood_donation.repositoty.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    public Blog create(Blog blog) {
        blog.setIsDeleted(false);
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
        return blogRepository.save(blog);
    }
    public void delete(Long id) {
        Blog blog = getById(id);
        blog.setIsDeleted(true);
        blogRepository.save(blog);
    }
}
