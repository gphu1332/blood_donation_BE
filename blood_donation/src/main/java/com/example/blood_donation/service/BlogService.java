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
        return blogRepository.save(blog);
    }
    public List<Blog> getAll() {
        return blogRepository.findAll();
    }
    public Blog getById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
    }
    public Blog update(Long id, Blog updated) {
        Blog blog = getById(id);
        blog.setContTitle(updated.getContTitle());
        blog.setContType(updated.getContType());
        blog.setContBody(updated.getContBody());
        return blogRepository.save(blog);
    }
    public void delete(Long id) {
        blogRepository.deleteById(id);
    }
}
