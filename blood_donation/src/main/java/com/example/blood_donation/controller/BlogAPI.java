package com.example.blood_donation.controller;

import com.example.blood_donation.entity.Blog;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.service.BlogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@SecurityRequirement(name = "api")
public class BlogAPI {

    @Autowired
    private BlogService blogService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Blog> create(@ModelAttribute Blog blog) {
        return ResponseEntity.ok(blogService.create(blog));
    }

    @GetMapping
    public List<Blog> getAll() {
        return blogService.getAll();
    }

    @GetMapping("/{id}")
    public Blog getById(@PathVariable Long id) {
        return blogService.getById(id);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public Blog update(@PathVariable Long id, @ModelAttribute Blog blog) {
        return blogService.update(id, blog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        blogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
