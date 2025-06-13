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
    public Blog getById(Integer id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
    }
    public Blog update(Integer id, Blog updated) {
        Blog blog = getById(id);
        blog.setContTitle(updated.getContTitle());
        blog.setContType(updated.getContType());
        blog.setContBody(updated.getContBody());
        return blogRepository.save(blog);
    }
    public void delete(Integer id) {
        blogRepository.deleteById(id);
    }
}
