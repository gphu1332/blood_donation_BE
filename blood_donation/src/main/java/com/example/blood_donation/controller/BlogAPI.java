package com.example.blood_donation.controller;

import com.example.blood_donation.entity.Blog;
import com.example.blood_donation.service.BlogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@SecurityRequirement(name = "api")
public class BlogAPI {
    @Autowired
    private BlogService blogService;
    @PostMapping
    public ResponseEntity<Blog> create(@RequestBody Blog blog) {
        return ResponseEntity.ok(blogService.create(blog));
    }
    @GetMapping
    public List<Blog> getAll() {
        return blogService.getAll();
    }
    @GetMapping("/{id}")
    public Blog getById(@PathVariable Integer id) {
        return blogService.getById(id);
    }
    @PutMapping("/{id}")
    public Blog update(@PathVariable Integer id, @RequestBody Blog blog) {
        return blogService.update(id, blog);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        blogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
