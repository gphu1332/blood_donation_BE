package com.example.blood_donation.controller;

import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.repositoty.UserRepository;
import com.example.blood_donation.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "api")
public class UserAPI {
    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        User savedUser = userService.createUser(user);
        UserDTO responseDTO = modelMapper.map(savedUser, UserDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User existingUser = optionalUser.get();
        modelMapper.map(userDTO, existingUser);
        User updatedUser = userRepository.save(existingUser);
        UserDTO responseDTO = modelMapper.map(updatedUser, UserDTO.class);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}

